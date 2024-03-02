package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Domain.Validation.ValidationException;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Utils.GUIMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class UcController {

    private Service serv;

    private User user;

    private Stage stage;

    @FXML
    private TextField userFNF;

    @FXML
    private TextField userLNF;

    @FXML
    public void initialize(){
    }

    public void setService(Service serv, Stage stage, User user){
        this.serv = serv;
        this.stage = stage;
        this.user = user;

        if(user != null){
            setFields(user);
        }

    }

    private void setFields(User user) {

        userFNF.setText(user.getFirstName());
        userLNF.setText(user.getLastName());

    }


    @FXML
    public void handleUCOKButton(ActionEvent actionEvent) {

        String first_name = userFNF.getText();
        String last_name = userLNF.getText();

        if(user == null)
            addUser(first_name, last_name);
        else
            updateUser(first_name, last_name);
    }

    private void addUser(String firstName, String lastName) {

        try{
            var op = serv.addUser(firstName, lastName);
            if(op.isPresent())
                GUIMessage.showMessage(null, Alert.AlertType.INFORMATION, "Added", "User added succesfuly! Your id is " + op.get().getId() + ".\nYour password is 'implicit'.\nPlease change it as soon as you log in.");
            stage.close();
        }
        catch (ValidationException v){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", v.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateUser(String firstName, String lastName) {

        try{
            serv.updateUser(user.getId(), firstName, lastName, user.getPassword());
            GUIMessage.showMessage(null, Alert.AlertType.INFORMATION, "Updated", "User updated succesfuly!");
        }
        catch(ValidationException e){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        stage.close();
    }

    private void showUserWindow(User user) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("user-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserController userController = loader.getController();
            userController.setService(serv, stage, user);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
