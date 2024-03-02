package ir.map.socialnetworkapp.Repository.PagingRepositories;

import ir.map.socialnetworkapp.Domain.Friendship;
import ir.map.socialnetworkapp.Domain.Tuple;
import ir.map.socialnetworkapp.Repository.DBRepositories.FriendshipDBRepository;
import ir.map.socialnetworkapp.Repository.PagingUtils.Page;
import ir.map.socialnetworkapp.Repository.PagingUtils.PageObject;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendshipPagingDBRepository extends FriendshipDBRepository implements PagingRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipPagingDBRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<Friendship> findAll(PagingInformation pagingInfo) {
        
        Set<Friendship> frequests = new HashSet<>();

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

                Friendship fr = new Friendship();
                fr.setId(new Tuple<>(id1, id2));
                fr.setFriendsFrom(request_from);

                frequests.add(fr);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new PageObject<>(pagingInfo, frequests.stream());
    }
}
