package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.*;
import ir.map.socialnetworkapp.Repository.DBRepositories.FriendshipDBRepository;
import ir.map.socialnetworkapp.Repository.DBRepositories.FriendshipRequestDBRepository;
import ir.map.socialnetworkapp.Repository.DBRepositories.MessageDBRepository;
import ir.map.socialnetworkapp.Repository.DBRepositories.UserDBRepository;
import ir.map.socialnetworkapp.Repository.InMemory.InMemoryRepository;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;
//import ir.map.socialnetworkapp.UserInterface.UI;

public class Main {
    public static void main(String[] args) {

        /*String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";
        Repository<Long, User> repo_user1 = new InMemoryRepository<>();
        Repository<Tuple<Long, Long>, Friendship> repo_friendship1 = new InMemoryRepository<>();
        Repository<Long, User> repo_user = new UserDBRepository(url, username, password);
        Repository<Tuple<Long, Long>, Friendship> repo_friendship = new FriendshipDBRepository(url, username, password);
        Repository<Tuple<Long, Long>, FriendshipRequest> repo_friendshipreq = new FriendshipRequestDBRepository(url, username, password);
        Repository<Long, Message> repo_messages = new MessageDBRepository(url, username, password);
        UI ui = new UI(repo_user, repo_friendship, repo_friendshipreq, repo_messages);
        ui.run();*/
    }
}