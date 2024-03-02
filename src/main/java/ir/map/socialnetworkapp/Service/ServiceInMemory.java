package ir.map.socialnetworkapp.Service;



import ir.map.socialnetworkapp.Domain.*;
import ir.map.socialnetworkapp.Repository.PagingRepositories.FriendshipRequestPagingDBRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.MessageDBPagingRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.UserPagingDBRepository;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceInMemory extends Service {

    public ServiceInMemory(UserPagingDBRepository repo_user, Repository<Tuple<Long, Long>, Friendship> repo_friendship, FriendshipRequestPagingDBRepository repo_friendshipreq, MessageDBPagingRepository repo_messages) {
        super(repo_user, repo_friendship, repo_friendshipreq, repo_messages);
    }

    @Override
    public Optional<User> removeUser(Long id) {

        var op = super.removeUser(id);

        if (op.isPresent()) {

            User deleted = op.get();
            ArrayList<User> void_friends = new ArrayList<>();
            deleted.setFriends(void_friends);

            for (Friendship friendship : repo_friendship.findALL()) {

                if (friendship.getId().getLeft().equals(id)) {

                    if (repo_user.findOne(friendship.getId().getRight()).isPresent()) {
                        User friend = repo_user.findOne(friendship.getId().getRight()).get();

                        List<User> friendlist = friend.getFriends();
                        friendlist.remove(deleted);
                        friend.setFriends(friendlist);

                    }

                    repo_friendship.delete(friendship.getId());

                } else if (friendship.getId().getRight().equals(id)) {

                    if (repo_user.findOne(friendship.getId().getLeft()).isPresent()) {
                        User friend = repo_user.findOne(friendship.getId().getLeft()).get();

                        List<User> friendlist = friend.getFriends();
                        friendlist.remove(deleted);
                        friend.setFriends(friendlist);

                    }

                    repo_friendship.delete(friendship.getId());
                }
            }
        }

        return op;
    }

    @Override
    public Optional<Friendship> removeFriendship(Long id1, Long id2) throws NoSuchAlgorithmException {

        var op = super.removeFriendship(id1, id2);

        if(op.isEmpty())
             op = repo_friendship.delete(new Tuple<>(id2, id1));

        if (op.isPresent()) {

            if (repo_user.findOne(id1).isPresent() && repo_user.findOne(id2).isPresent()) {
                User u1 = repo_user.findOne(id1).get();
                User u2 = repo_user.findOne(id2).get();

                User u1_nofr = new User(u1.getFirstName(), u1.getLastName());
                u1_nofr.setId(u1.getId());

                User u2_nofr = new User(u2.getFirstName(), u2.getLastName());
                u2_nofr.setId(u2.getId());

                u1.getFriends().remove(u2_nofr);
                u2.getFriends().remove(u1_nofr);

            }
        }

        return op;
    }

    @Override
    public Optional<Friendship> addFriendship(Long id1, Long id2) throws NoSuchAlgorithmException {

        var op = super.addFriendship(id1, id2);

        if(op.isEmpty() && repo_user.findOne(id1).isPresent() && repo_user.findOne(id2).isPresent()) {

            User u1 = repo_user.findOne(id1).get();
            User u2 = repo_user.findOne(id2).get();

            User u1_nofr = new User(u1.getFirstName(), u1.getLastName());
            u1_nofr.setId(u1.getId());

            User u2_nofr = new User(u2.getFirstName(), u2.getLastName());
            u2_nofr.setId(u2.getId());

            List<User> fr1 = u1.getFriends();
            fr1.add(u2_nofr);
            u1.setFriends(fr1);

            List<User> fr2 = u2.getFriends();
            fr2.add(u1_nofr);
            u2.setFriends(fr2);
        }

        return op;
    }
}
