package ir.map.socialnetworkapp.Repository.PagingRepositories;

import ir.map.socialnetworkapp.Domain.Message;
import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Repository.DBRepositories.MessageDBRepository;
import ir.map.socialnetworkapp.Repository.PagingUtils.Page;
import ir.map.socialnetworkapp.Repository.PagingUtils.PageObject;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDBPagingRepository extends MessageDBRepository implements PagingRepository<Long, Message> {

    public MessageDBPagingRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Page<Message> findAll(PagingInformation pagingInfo) {

        Set<Message> messages = new HashSet<>();

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from messages " +
                        "limit ? offset ?");

        ) {

            statement.setInt(1, pagingInfo.getPageSize());
            statement.setInt(2, (pagingInfo.getPageNumber() - 1) * pagingInfo.getPageSize());

            ResultSet result = statement.executeQuery();
            while (result.next()) {

                Long id = result.getLong("id");
                Long id_from = result.getLong("id_from");
                List<Long> to = getReceivers(id);
                String text = result.getString("text");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                List<Message> replies = getReplies(id);
                long is_reply_to = result.getLong("is_reply_to");

                Message message = new Message(id_from, to, text);
                message.setId(id);
                message.setDate(date);
                message.setReplies(replies);

                if(is_reply_to == 0L)
                    message.setIs_reply_to(null);
                else message.setIs_reply_to(is_reply_to);

                messages.add(message);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new PageObject<>(pagingInfo, messages.stream());

    }

    public Page<Message> findAllBetweenTwo(PagingInformation pagingInfo, User user1, User user2) {

        Set<Message> messages = new HashSet<>();

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * " +
                        "from messages m inner join message_destinations md " +
                        "on m.id = md.id_message and (m.id_from = ? and md.id_user = ? or m.id_from = ? and md.id_user = ?) " +
                        "order by m.date desc "+
                        "limit ?");

        ) {

            statement.setLong(1, user1.getId());
            statement.setLong(2, user2.getId());
            statement.setLong(3, user2.getId());
            statement.setLong(4, user1.getId());

            statement.setInt(5, pagingInfo.getPageSize());

            ResultSet result = statement.executeQuery();
            while (result.next()) {

                Long id = result.getLong("id");
                Long id_from = result.getLong("id_from");
                List<Long> to = getReceivers(id);
                String text = result.getString("text");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                List<Message> replies = getReplies(id);
                long is_reply_to = result.getLong("is_reply_to");

                Message message = new Message(id_from, to, text);
                message.setId(id);
                message.setDate(date);
                message.setReplies(replies);

                if(is_reply_to == 0L)
                    message.setIs_reply_to(null);
                else message.setIs_reply_to(is_reply_to);

                messages.add(message);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new PageObject<>(pagingInfo, messages.stream());

    }
}
