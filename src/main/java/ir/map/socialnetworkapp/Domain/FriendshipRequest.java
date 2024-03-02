package ir.map.socialnetworkapp.Domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendshipRequest extends Entity<Tuple<Long, Long>>{

    private String status;

    private LocalDateTime request_from;

    public FriendshipRequest() {
        this.status = "pending";
        this.request_from = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequest_from() {
        return request_from;
    }

    public void setRequest_from(LocalDateTime request_from) {
        this.request_from = request_from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendshipRequest that = (FriendshipRequest) o;
        return Objects.equals(status, that.status) && Objects.equals(request_from, that.request_from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status, request_from);
    }
}
