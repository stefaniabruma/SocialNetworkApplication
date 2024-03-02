package ir.map.socialnetworkapp.Repository.PagingRepositories;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Repository.DBRepositories.UserDBRepository;
import ir.map.socialnetworkapp.Repository.PagingUtils.Page;
import ir.map.socialnetworkapp.Repository.PagingUtils.PageObject;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformationObject;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.PagingRepository;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class UserPagingDBRepository extends UserDBRepository implements PagingRepository<Long, User> {


    public UserPagingDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<User> findAll(PagingInformation pagingInfo) {

        Set<User> users = new HashSet<>();


        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                        "limit ? offset ?")
            ) {

            statement.setInt(1, pagingInfo.getPageSize());
            statement.setInt(2, (pagingInfo.getPageNumber() - 1) * pagingInfo.getPageSize());

            ResultSet result = statement.executeQuery();

            while(result.next()){

                Long id = result.getLong("id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String passwd = result.getString("password");

                User user = new User(firstName, lastName);
                user.setId(id);
                user.setPassword(passwd);

                var friends = getFriends(user);
                user.setFriends(friends);

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return new PageObject<>(pagingInfo, users.stream());
    }


    public Optional<User> findOne(Long id, PagingInformation pagingInfo){

        if(id == null)
            throw new IllegalArgumentException("Id must not be null!\n");

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from users "
                        + "where id = ?")
        ) {

            statement.setInt(1, Math.toIntExact(id));

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String password = result.getString("password");

                User user = new User(firstName, lastName);
                user.setId(id);
                user.setPassword(password);

                List<User> friends = getFriends(user, pagingInfo);
                user.setFriends(friends);

                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    protected ArrayList<User> getFriends(User user, PagingInformation pagingInfo) {

        ArrayList<User> friends = new ArrayList<>();

        Long id = user.getId();

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement2 = connection.prepareStatement("select distinct id_user1, id_user2, first_name, last_name " +
                        "from friendships inner join users " +
                        "on users.id = friendships.id_user1 and friendships.id_user2 = ? or users.id = friendships.id_user2 and friendships.id_user1 = ? " +
                        "limit ? offset ?"
                        )
        ) {

            statement2.setInt(1, Math.toIntExact(id));
            statement2.setInt(2, Math.toIntExact(id));
            statement2.setInt(3, pagingInfo.getPageSize());
            statement2.setInt(4, (pagingInfo.getPageNumber()  - 1) * pagingInfo.getPageSize());

            ResultSet result2 = statement2.executeQuery();

            while (result2.next()) {
                long id_friend = 0L;
                if (id == result2.getLong("id_user1"))
                    id_friend = result2.getLong("id_user2");
                else if (id == result2.getLong("id_user2"))
                    id_friend = result2.getLong("id_user1");

                String first_name = result2.getString("first_name");
                String last_name = result2.getString("last_name");

                User fr = new User(first_name, last_name);
                fr.setId(id_friend);

                friends.add(fr);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return friends;
    }
}
