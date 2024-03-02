package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformationObject;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Utils.GUIMessage;
import ir.map.socialnetworkapp.Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.List;



public class UserController implements Observer {
    private Service serv;
    private ObservableList<User> model = FXCollections.observableArrayList();
    private Stage stage;
    private PagingInformation pagingInfo = new PagingInformationObject(1, 10);

    @FXML
    private TableView<User> userTV;
    @FXML
    private TableColumn<User, String> userIDC;
    @FXML
    private TableColumn<User, String> userFNC;
    @FXML
    private TableColumn<User, String> userLNC;
    @FXML
    private Label labelWelcome;
    @FXML
    private TextField textFieldPageSize;


    private User user;

    private void initModel() {

        model.setAll(serv.showFriendsPage(pagingInfo, user).getContent().toList());

    }

    public void setService(Service serv, Stage stage, User user){
        this.serv = serv;
        this.user = user;
        this.stage = stage;

        serv.addObserver(this);

        labelWelcome.setText("Welcome back, " + user.getFirstName() + " " + user.getLastName() + "!");
        initModel();
    }

    @FXML
    public void initialize(){
        userIDC.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
        userFNC.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        userLNC.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        userTV.setItems(model);

        textFieldPageSize.setText(String.valueOf(pagingInfo.getPageSize()));
        textFieldPageSize.textProperty().addListener(o -> handlePageSizeChange());

    }

    @Override
    public void update() {

        initModel();

    }

    @FXML
    public void handleDeleteUser(ActionEvent ev){

            if(user != null) {
                var userFromRepo_op = serv.removeUser(user.getId());
                if(userFromRepo_op.isPresent())
                    GUIMessage.showMessage(null, Alert.AlertType.INFORMATION, "Deleted", "The user was deleted succesfully!\n");

                stage.close();
            }
            else GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "No user selected!");


    }


    @FXML
    public void handleUpdateUser(ActionEvent ev){

        if(user != null)
            showUserUCDDialog(user);
        else{
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "No user selected!");
        }

    }

    @FXML
    public void handleSeeAllUsers(){
        showAllUsersDialog();
    }

    private void showAllUsersDialog() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("allusers-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AllUsersController auController = loader.getController();
            auController.setService(serv, user);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void showUserUCDDialog(User user) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("uc-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UcController userUCController = loader.getController();
            userUCController.setService(serv, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleUpdateCredentials(ActionEvent ev) {

        showUpdateCredentialsDialog();

    }

    private void showUpdateCredentialsDialog() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("updatecredentials-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UpdateCredentialsController updateCredentialsController = loader.getController();
            updateCredentialsController.setService(serv, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void handleSeeAllRequests(ActionEvent actionEvent) {
        
        showFriendshipRequestsDialog();
    }

    private void showFriendshipRequestsDialog() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("friendrequests-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestsController frController = loader.getController();
            frController.setService(serv, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleLogOut(ActionEvent actionEvent){

        showLogInDialog();
        stage.close();
    }

    private void showLogInDialog() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("login-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginController loginController = loader.getController();
            loginController.setService(serv, dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleNext(){

        if(serv.showFriendsPage(new PagingInformationObject(pagingInfo.getPageNumber() + 1, pagingInfo.getPageSize()), user).getContent().toList().isEmpty())
            return;

        pagingInfo = new PagingInformationObject(pagingInfo.getPageNumber() + 1, pagingInfo.getPageSize());
        initModel();
    }

    @FXML
    public void handlePrevious(){

        if(pagingInfo.getPageNumber() == 1)
            return;

        pagingInfo = new PagingInformationObject(pagingInfo.getPageNumber() - 1, pagingInfo.getPageSize());
        initModel();
    }

    @FXML
    public void handlePageSizeChange(){

        if(textFieldPageSize.getText().isEmpty())
            return;

        int pageSize = Integer.parseInt(textFieldPageSize.getText());

        pagingInfo = new PagingInformationObject(1, pageSize);
        initModel();
    }
}