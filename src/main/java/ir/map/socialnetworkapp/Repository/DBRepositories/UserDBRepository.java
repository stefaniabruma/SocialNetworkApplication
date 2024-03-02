package ir.map.socialnetworkapp.Repository.DBRepositories;



import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Domain.Validation.UserValidator;
import ir.map.socialnetworkapp.Domain.Validation.Validator;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class UserDBRepository implements Repository<Long, User> {
    protected final String url;
    protected final String username;
    protected final String password;
    private final Validator<User> v_user = new UserValidator();

    public UserDBRepository(String url, String username, String password) {

        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(Long id) {

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

                List<User> friends = getFriends(user);
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

    @Override
    public Iterable<User> findALL() {

        Set<User> users = new HashSet<>();

        try (
             Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users ");
             ResultSet result = statement.executeQuery();
        ) {

            while (result.next()) {

                Long id = result.getLong("id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String password = result.getString("password");

                User user = new User(firstName, lastName);
                user.setId(id);
                user.setPassword(password);

                ArrayList<User> friends = getFriends(user);
                user.setFriends(friends);

                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public Optional<User> save(User entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null\n");

        v_user.validate(entity);

        try (
             Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("insert " +
                     "into users(first_name, last_name, password) " +
                     "values (?, ?, ?) " +
                     "returning id");
        ) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getPassword());

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                Long id = result.getLong("id");
                entity.setId(id);

                return Optional.of(entity);
            }
            else return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        if(id == null)
            throw new IllegalArgumentException("ID must not be null!\n");

        try(
           Connection connection = DriverManager.getConnection(url, username, password);
           PreparedStatement statement = connection.prepareStatement("delete from users " +
                   "where id = ?")
        ) {

            statement.setInt(1, Math.toIntExact(id));

            var user = findOne(id);
            statement.executeUpdate();

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null!\n");
        }

        v_user.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update users " +
                     "set first_name = ?, last_name = ?, password = ? " +
                     "where id = ?");
        ) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getPassword());
            statement.setLong(4, entity.getId());

            return statement.executeUpdate() == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    protected ArrayList<User> getFriends(User user) {

        ArrayList<User> friends = new ArrayList<>();

        Long id = user.getId();

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement2 = connection.prepareStatement("select distinct id_user1, id_user2, first_name, last_name " +
                        "from friendships inner join users " +
                        "on users.id = friendships.id_user1 and friendships.id_user2 = ? or users.id = friendships.id_user2 and friendships.id_user1 = ?")
        ) {

            statement2.setInt(1, Math.toIntExact(id));
            statement2.setInt(2, Math.toIntExact(id));

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
