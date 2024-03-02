package ir.map.socialnetworkapp.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long>{

    private Long from;
    private List<Long> to;
    private String text;
    private LocalDateTime date;
    private List<Message> replies;
    private Long is_reply_to;

    public Message(Long from, List<Long> to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = LocalDateTime.now();
        this.replies = new ArrayList<>();
        this.is_reply_to = null;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }

    public void appendTo(Long user){
        this.to.add(user);
    }

    public void appendReply(Message reply){
        replies.add(reply);
    }

    public Long getIs_reply_to() {
        return is_reply_to;
    }

    public void setIs_reply_to(Long is_reply_to) {
        this.is_reply_to = is_reply_to;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", replies=" + replies +
                ", is_reply_to=" + is_reply_to +
                '}';
    }
}
