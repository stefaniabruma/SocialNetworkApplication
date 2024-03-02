package ir.map.socialnetworkapp.Repository.DBRepositories;

import ir.map.socialnetworkapp.Domain.Message;
import ir.map.socialnetworkapp.Domain.Validation.MessageValidator;
import ir.map.socialnetworkapp.Domain.Validation.Validator;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepository implements Repository<Long, Message> {

    protected final String url;
    protected final String username;
    protected final String password;
    protected final Validator<Message> v_message = new MessageValidator();

    public MessageDBRepository(String url, String username, String password) {

        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long id) {

        if(id == null)
            throw new IllegalArgumentException("Id must not be null!\n");

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from Messages "
                        + "where id = ?")
        ) {

            statement.setInt(1, Math.toIntExact(id));
            ResultSet result = statement.executeQuery();

            if (result.next()) {

                Long id_from = result.getLong("id_from");
                List<Long> to = getReceivers(id);
                String text = result.getString("text");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                List<Message> replies = getReplies(id);
                Long is_reply_to = result.getLong("is_reply_to");
                
                Message message = new Message(id_from, to, text);
                message.setId(id);
                message.setDate(date);
                message.setReplies(replies);
                message.setIs_reply_to(is_reply_to);

                return Optional.of(message);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

   

    @Override
    public Iterable<Message> findALL() {

        Set<Message> messages = new HashSet<>();

        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select * from Messages ");
                ResultSet result = statement.executeQuery();
        ) {

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

        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null\n");

        v_message.validate(entity);

        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("insert " +
                    "into messages(id_from, text, date, is_reply_to) " +
                    "values (?, ?, ?, ?) " +
                    "returning id");
            PreparedStatement statement2 = connection.prepareStatement("insert " +
                    "into message_destinations(id_message, id_user) " +
                    "values (?, ?)");
        ) {


            statement.setLong(1, entity.getFrom());
            statement.setString(2, entity.getText());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            if(entity.getIs_reply_to() != null)
                statement.setLong(4, entity.getIs_reply_to());
            else statement.setNull(4, Types.BIGINT);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                Long id = result.getLong("id");
                entity.setId(id);

                entity.getTo().forEach(x -> {
                    try {
                        statement2.setLong(1, entity.getId());
                        statement2.setLong(2, x);
                        statement2.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                return Optional.of(entity);
            }
            else return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long id) {
        if(id == null)
            throw new IllegalArgumentException("ID must not be null!\n");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("delete from Messages " +
                        "where id = ?")
        ) {

            statement.setInt(1, Math.toIntExact(id));

            var Message = findOne(id);
            statement.executeUpdate();

            return Message;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    protected List<Long> getReceivers(Long id) {
        
        List<Long> receivers= new ArrayList<>();
        
        try(
                
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select id_user from message_destinations " +
                    "where id_message = ?")
            
                ) {
            
            statement.setLong(1, id);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){

                Long user = result.getLong("id_user");
                receivers.add(user);
                
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return receivers;
    }
    
    protected List<Message> getReplies(Long id) {

        List<Message> replies = new ArrayList<>();

        try(
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from messages " +
                    "where is_reply_to = ?");
            ) {

            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();

            if(result.next()){

                Long id_message = result.getLong("id");
                Long id_from = result.getLong("id_from");
                List<Long> to = getReceivers(id_message);
                String text = result.getString("text");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();

                Message reply = new Message(id_from, to, text);
                reply.setId(id_message);
                reply.setDate(date);

                replies.add(reply);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return replies;

    }

    
}
