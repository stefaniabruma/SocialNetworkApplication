package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.*;
import ir.map.socialnetworkapp.Repository.DBRepositories.*;
import ir.map.socialnetworkapp.Repository.PagingRepositories.FriendshipRequestPagingDBRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.MessageDBPagingRepository;
import ir.map.socialnetworkapp.Repository.PagingRepositories.UserPagingDBRepository;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Service.ServiceDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class StartApplication extends Application {
    Service serv;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "postgres";

        UserPagingDBRepository repo_user = new UserPagingDBRepository(url, username, password);
        Repository<Tuple<Long, Long>, Friendship> repo_friendship = new FriendshipDBRepository(url, username, password);
        FriendshipRequestPagingDBRepository repo_friendshipreq = new FriendshipRequestPagingDBRepository(url, username, password);
        MessageDBPagingRepository repo_messages = new MessageDBPagingRepository(url, username, password);
        serv = new ServiceDB(repo_user, repo_friendship, repo_friendshipreq, repo_messages);

        initView(stage);
        stage.show();
    }

    private void initView(Stage stage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("login-view.fxml"));
        AnchorPane userLayout = userLoader.load();
        stage.setScene(new Scene(userLayout));
        stage.setTitle("SocialNetworkApp");

        LoginController loginController = userLoader.getController();
        loginController.setService(serv, stage);
    }


}