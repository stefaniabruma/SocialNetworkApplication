package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.Entity;
import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Domain.Validation.ValidationException;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformationObject;
import ir.map.socialnetworkapp.Service.FoundException;
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

public class AllUsersController implements Observer {

    private Service serv;

    private User user;

    private ObservableList<User> model = FXCollections.observableArrayList();

    private PagingInformation pagingInfo = new PagingInformationObject(1, 10);

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TableColumn<User, String> columnId;

    @FXML
    private TableColumn<User, String> columnFirstName;

    @FXML
    private TableColumn<User, String> columnLastName;
    @FXML
    private TextField textFieldMessage;

    @FXML
    private TextField textFieldPageSize;

    private void initModel() {

        model.setAll(serv.showUsersPage(pagingInfo).getContent().toList());

    }

    public void setService(Service serv, User user){
        this.serv = serv;
        this.user = user;
        serv.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){

        columnId.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableViewUsers.setItems(model);
        tableViewUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        textFieldPageSize.setText(String.valueOf(pagingInfo.getPageSize()));
        textFieldPageSize.textProperty().addListener(o -> handlePageSizeChange());

    }

    @Override
    public void update() {
        initModel();
    }

    public void handleSendFriendRequest(ActionEvent actionEvent) {

        User friend = tableViewUsers.getSelectionModel().getSelectedItem();

        if(friend == null){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "No user selected!");
            return;
        }

        try {
            serv.addFriendshipRequest(user.getId(), friend.getId());
            GUIMessage.showMessage(null, Alert.AlertType.INFORMATION, "Info", "Request sent!");
        }
        catch(FoundException | ValidationException e){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }

    }

    @FXML
    public void handleOpenChat(ActionEvent ev){

        User receiver = tableViewUsers.getSelectionModel().getSelectedItem();

        showChatDialog(receiver);

    }

    private void showChatDialog(User receiver) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("chat-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ChatController chatController = loader.getController();
            chatController.setService(serv, dialogStage, user, receiver);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSendToAll(){
        String messageText = textFieldMessage.getText();
        var receivers = tableViewUsers.getSelectionModel().getSelectedItems().stream()
                .map(Entity::getId)
                .toList();

        if(receivers.isEmpty())
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "No user selected!");

        try{
            serv.addMessage(user.getId(), receivers, messageText, null);
            GUIMessage.showMessage(null, Alert.AlertType.INFORMATION, "Info", "Message sent!");
            textFieldMessage.clear();

        }
        catch (ValidationException e){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    public void handleNext(){

        if(serv.showUsersPage(new PagingInformationObject(pagingInfo.getPageNumber() + 1, pagingInfo.getPageSize())).getContent().toList().isEmpty())
            return;

        pagingInfo = new PagingInformationObject(pagingInfo.getPageNumber() + 1, pagingInfo.getPageSize());
        initModel();
    }

    public void handlePrevious(){

        if(pagingInfo.getPageNumber() == 1)
            return;

        pagingInfo = new PagingInformationObject(pagingInfo.getPageNumber() - 1, pagingInfo.getPageSize());
        initModel();
    }

    public void handlePageSizeChange(){

        if(textFieldPageSize.getText().isEmpty())
            return;

        int pageSize = Integer.parseInt(textFieldPageSize.getText());

        pagingInfo = new PagingInformationObject(1, pageSize);
        initModel();
    }
}
