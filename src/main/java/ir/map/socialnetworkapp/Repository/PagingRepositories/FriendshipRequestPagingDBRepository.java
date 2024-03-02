package ir.map.socialnetworkapp.Repository.PagingRepositories;

import ir.map.socialnetworkapp.Domain.FriendshipRequest;
import ir.map.socialnetworkapp.Domain.Tuple;
import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Repository.DBRepositories.FriendshipRequestDBRepository;
import ir.map.socialnetworkapp.Repository.PagingUtils.Page;
import ir.map.socialnetworkapp.Repository.PagingUtils.PageObject;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendshipRequestPagingDBRepository extends FriendshipRequestDBRepository implements PagingRepository<Tuple<Long, Long>, FriendshipRequest> {

    public FriendshipRequestPagingDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page findAll(PagingInformation pagingInfo) {

        Set<FriendshipRequest> fr_requests = new HashSet<>();

        try(

                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from friendship_requests " +
                        "limit ? offset ?");

        ) {

            statement.setInt(1, pagingInfo.getPageSize());
            statement.setInt(2, (pagingInfo.getPageNumber() - 1) * pagingInfo.getPageSize());

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
        return new PageObject<>(pagingInfo, fr_requests.stream());
    }

    public Page<FriendshipRequest> findAllPendingPage(PagingInformation pagingInfo, User user){

        Set<FriendshipRequest> fr_requests = new HashSet<>();

        try(

                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from friendship_requests " +
                        "where id_user2 = ? and status = 'pending'" +
                        "limit ? offset ?");

        ) {

            statement.setLong(1, user.getId());
            statement.setInt(2, pagingInfo.getPageSize());
            statement.setInt(3, (pagingInfo.getPageNumber() - 1) * pagingInfo.getPageSize());

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
        return new PageObject<>(pagingInfo, fr_requests.stream());

    }


}
