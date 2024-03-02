package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Domain.Validation.ValidationException;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformation;
import ir.map.socialnetworkapp.Repository.PagingUtils.PagingInformationObject;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Domain.Message;
import ir.map.socialnetworkapp.Utils.GUIMessage;
import ir.map.socialnetworkapp.Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Observer {

    private Service serv;
    private Stage stage;
    private User user1;
    private User user2;
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private PagingInformation pagingInfo = new PagingInformationObject(1, 13);
    private int lastMessagesCount = 13;

    @FXML
    private TextField textFieldMessage;
    @FXML
    private ListView<Message> listViewMessages;

    public void setService(Service serv, Stage stage, User user1, User user2){
        this.serv = serv;
        this.stage = stage;
        this.user1 = user1;
        this.user2 = user2;

        serv.addObserver(this);
        initModel();

    }

    private void initModel() {


        messages.setAll(serv.showMessagesPageBetweenTwo(pagingInfo, user1, user2).getContent().toList());
    }

    @Override
    public void update() {
        initModel();
    }

    @FXML
    public void initialize(){

        listViewMessages.setCellFactory(x -> new ListCell<Message>() {
                @Override
                protected void updateItem(Message item, boolean empty){
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    if(item.getIs_reply_to() == null) {
                        setText(serv.findUser(item.getFrom()).get().getFirstName() + ": " + item.getText());
                    }
                    else{

                        if(serv.findMessage(item.getIs_reply_to()).isEmpty() || serv.findUser(item.getFrom()).isEmpty()) {
                            setText("null");
                            return;
                        }

                        setText("Re: " + serv.findUser(serv.findMessage(item.getIs_reply_to()).get().getFrom()).get().getFirstName() +": "+ serv.findMessage(item.getIs_reply_to()).get().getText() + " " + serv.findUser(item.getFrom()).get().getFirstName() + ": " + item.getText());
                    }
                }
            });
        listViewMessages.setItems(messages);

    }

    @FXML
    public void handleSendMessage() {

        String text = textFieldMessage.getText();
        textFieldMessage.clear();

        Message selected = listViewMessages.getSelectionModel().getSelectedItem();

        try {
            if (selected != null)
                serv.addMessage(user1.getId(), new ArrayList<>(Collections.singletonList(user2.getId())), text, selected.getId());
            else
                serv.addMessage(user1.getId(), new ArrayList<>(Collections.singletonList(user2.getId())), text, null);

        } catch (ValidationException e) {
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
}
