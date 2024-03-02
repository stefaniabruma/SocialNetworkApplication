package ir.map.socialnetworkapp.Domain;


import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long,Long>>{
    private LocalDateTime friendsFrom;

    public Friendship() {
        friendsFrom = LocalDateTime.now();
    }

    public Friendship(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "date=" + friendsFrom +
                ", id=" + id +
                '}';
    }
}
