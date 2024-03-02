package ir.map.socialnetworkapp.Service;


import ir.map.socialnetworkapp.Domain.*;
import ir.map.socialnetworkapp.Repository.PagingRepositories.FriendshipRequestPagingDBRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.MessageDBPagingRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.UserPagingDBRepository;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class ServiceDB extends Service {

    public ServiceDB(UserPagingDBRepository repo_user, Repository<Tuple<Long, Long>, Friendship> repo_friendship, FriendshipRequestPagingDBRepository repo_friendshipreq, MessageDBPagingRepository repo_messages) {
        super(repo_user, repo_friendship, repo_friendshipreq, repo_messages);
    }

    @Override
    public Optional<Friendship> addFriendship(Long id1, Long id2) throws NoSuchAlgorithmException {
        var op =  super.addFriendship(id1, id2);

        notifyObservers();

        return op;
    }

    @Override
    public Optional<User> removeUser(Long id) {
        var op = super.removeUser(id);

        if(op.isPresent())
            notifyObservers();

        return op;
    }

    @Override
    public Optional<User> updateUser(Long id, String firstName, String lastName, String password) throws NoSuchAlgorithmException {

        var op = super.updateUser(id, firstName, lastName, password);

        if(op.isEmpty())
            notifyObservers();

        return op;
    }

    @Override
    public Optional<FriendshipRequest> addFriendshipRequest(Long id1, Long id2) {
        var op =  super.addFriendshipRequest(id1, id2);

        notifyObservers();

        return op;

    }

    @Override
    public Optional<FriendshipRequest> updateFriendshipRequest(Long id1, Long id2, String status) {
        var op = super.updateFriendshipRequest(id1, id2, status);

        if(op.isEmpty())
            notifyObservers();

        return op;
    }
}