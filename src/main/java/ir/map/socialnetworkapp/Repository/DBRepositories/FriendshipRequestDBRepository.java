package ir.map.socialnetworkapp.Repository.DBRepositories;

import ir.map.socialnetworkapp.Domain.FriendshipRequest;
import ir.map.socialnetworkapp.Domain.Tuple;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipRequestDBRepository implements Repository<Tuple<Long, Long>, FriendshipRequest> {

    protected String url;
    protected String username;
    protected String password;

    public FriendshipRequestDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<FriendshipRequest> findOne(Tuple<Long, Long> id) {

        if(id == null)
            throw new IllegalArgumentException("Id must not be null!");

        try(

            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendship_requests " +
                    "where (id_user1 = ? and id_user2 = ? or id_user1 = ? and id_user2 = ?) " +
                    "and status = ?");

            ) {

            statement.setInt(1, Math.toIntExact(id.getLeft()));
            statement.setInt(2, Math.toIntExact(id.getRight()));
            statement.setInt(3, Math.toIntExact(id.getRight()));
            statement.setInt(4, Math.toIntExact(id.getLeft()));
            statement.setString(5, "pending");

            ResultSet result = statement.executeQuery();

            if(result.next()){

                FriendshipRequest fr_r = new FriendshipRequest();
                fr_r.setStatus("pending");

                return Optional.of(fr_r);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<FriendshipRequest> findALL() {

        Set<FriendshipRequest> fr_requests = new HashSet<>();

        try(

            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendship_requests");

        ) {

            ResultSet result = statement.executeQuery();

            while(result.next()){

                Long id1 = result.getLong("id_user1");
                Long id2 = result.getLong("id_user2");
                String status = result.getString("status");
                LocalDateTime request_from = result.getTimestamp("request_from").toLocalDateTime();

                FriendshipRequest fr_r = new FriendshipRequest();
                fr_r.setId(new Tuple<>(id1, id2));
                fr_r.setStatus(status);
                fr_r.setRequest_from(request_from);

                fr_requests.add(fr_r);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fr_requests;
    }

    @Override
    public Optional<FriendshipRequest> save(FriendshipRequest entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must not bee null!");

        try(

            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("insert into " +
                        "friendship_requests(id_user1, id_user2, status, request_from) " +
                        "values(?, ?, ?, ?)")

            ){

            statement.setInt(1, Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2, Math.toIntExact(entity.getId().getRight()));
            statement.setString(3, entity.getStatus());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getRequest_from()));

            return statement.executeUpdate() == 0 ? Optional.empty() : Optional.of(entity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendshipRequest> delete(Tuple<Long, Long> id) {

        if(id == null)
            throw new IllegalArgumentException("Id must not be null!");

        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("delete from friendship_requests " +
                        "where (id_user1 = ? and id_user2 = ? or id_user1 = ? and id_user2 = ?) " +
                        "and status = ?")
            ) {

            statement.setInt(1, Math.toIntExact(id.getLeft()));
            statement.setInt(2, Math.toIntExact(id.getRight()));
            statement.setInt(3, Math.toIntExact(id.getRight()));
            statement.setInt(4, Math.toIntExact(id.getLeft()));
            statement.setString(5, "pending");

            var fr_r = findOne(id);
            statement.executeUpdate();

            return fr_r;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<FriendshipRequest> update(FriendshipRequest entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must not bee null!");

        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("update friendship_requests " +
                        "set status = ? " +
                        "where id_user1 = ? and id_user2 = ? and status = ? ")
            ) {

            statement.setString(1, entity.getStatus());
            statement.setInt(2, Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(3, Math.toIntExact(entity.getId().getRight()));
            statement.setString(4, "pending");

            return statement.executeUpdate() == 0 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
