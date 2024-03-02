package ir.map.socialnetworkapp.Repository.DBRepositories;


import ir.map.socialnetworkapp.Domain.Friendship;
import ir.map.socialnetworkapp.Domain.Tuple;
import ir.map.socialnetworkapp.Domain.Validation.FriendshipValidator;
import ir.map.socialnetworkapp.Domain.Validation.Validator;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    protected final String url;
    protected final String username;
    protected final String password;
    protected final Validator<Friendship> v_friendship = new FriendshipValidator();

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {

        if(id == null)
            throw new IllegalArgumentException("Id must not be null!\n");

        try(

            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships " +
                    "where id_user1 = ? and id_user2 = ? " +
                    "or id_user1 = ? and id_user2 = ?")

        ) {

            statement.setInt(1, Math.toIntExact(id.getRight()));
            statement.setInt(2, Math.toIntExact(id.getLeft()));
            statement.setInt(4, Math.toIntExact(id.getRight()));
            statement.setInt(3, Math.toIntExact(id.getLeft()));

            ResultSet result = statement.executeQuery();

            if(result.next()){

                LocalDateTime friendsFrom = result.getTimestamp("friends_from").toLocalDateTime();

                Friendship fr = new Friendship(friendsFrom);
                fr.setId(id);

                return Optional.of(fr);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findALL() {

        Set<Friendship> friendships = new HashSet<>();

        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from Friendships");
            ResultSet result = statement.executeQuery();
        ){

            while(result.next()){

                Long id1 = result.getLong("id_user1");
                Long id2 = result.getLong("id_user2");
                LocalDateTime date = result.getTimestamp("friends_from").toLocalDateTime();

                Friendship fr = new Friendship(date);
                fr.setId(new Tuple<>(id1, id2));

                friendships.add(fr);

              }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null!\n");

        v_friendship.validate(entity);

        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("insert " +
                    "into friendships(id_user1, id_user2, friends_from) " +
                    "values (?, ?, ?)");
        ) {

            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));

            return statement.executeUpdate() == 0 ? Optional.empty() : Optional.of(entity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {

        if(id == null)
            throw new IllegalArgumentException("Id must not be null!\n");

        try(

            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("delete from friendships " +
                        "where id_user1 = ? and id_user2  = ? or id_user1 = ? and id_user2 = ?")

        ) {

            statement.setInt(1, Math.toIntExact(id.getLeft()));
            statement.setInt(2, Math.toIntExact(id.getRight()));
            statement.setInt(3, Math.toIntExact(id.getRight()));
            statement.setInt(4, Math.toIntExact(id.getLeft()));

            var friendship = findOne(id);
            statement.executeUpdate();

            return friendship;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> update(Friendship entity) {

        if(entity == null){
            throw  new IllegalArgumentException("Entity must not be null!\n");
        }

        v_friendship.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("update friendships " +
                "set friends_from = ? " +
                "where id_user1 = ? and id_user2 = ?");
        ) {

            statement.setTimestamp(1, Timestamp.valueOf(entity.getFriendsFrom()));;
            statement.setLong(2, entity.getId().getLeft());
            statement.setLong(3, entity.getId().getRight());

            return statement.executeUpdate() == 0 ? Optional.of(entity) :  Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
