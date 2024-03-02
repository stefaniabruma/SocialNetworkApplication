package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformationObject;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer {

    private Service serv;
    private User user;
    private Stage stage;
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
    private TextField textFieldPageSize;

    private void initModel() {

        model.setAll(serv.showFriendRequestsPage(pagingInfo, user).getContent().toList());
    }

    public void setService(Service serv, Stage stage, User user){
        this.serv = serv;
        this.user = user;
        this.stage = stage;
        serv.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){

        columnId.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableViewUsers.setItems(model);

        textFieldPageSize.setText(String.valueOf(pagingInfo.getPageSize()));
        textFieldPageSize.textProperty().addListener(o -> handlePageSizeChange());

    }

    @Override
    public void update() {
        initModel();
    }


    @FXML
    public void handleAccept(ActionEvent actionEvent) throws NoSuchAlgorithmException {

        User friend = tableViewUsers.getSelectionModel().getSelectedItem();

        serv.updateFriendshipRequest(friend.getId(), user.getId(), "accepted");
        serv.addFriendship(friend.getId(), user.getId());

        stage.close();

    }

    @FXML
    public void handleDecline(ActionEvent actionEvent) {

        User friend = tableViewUsers.getSelectionModel().getSelectedItem();

        serv.updateFriendshipRequest(friend.getId(), user.getId(), "declined");

    }

    public void handleNext(){

        if(serv.showFriendRequestsPage(new PagingInformationObject(pagingInfo.getPageNumber() + 1, pagingInfo.getPageSize()), user).getContent().toList().isEmpty())
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
