package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Utils.GUIMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class LoginController {

    Service serv;

    @FXML
    TextField textFieldId;

    @FXML
    PasswordField textFieldPassword;

    private Stage stage;

    public void setService(Service serv, Stage stage){
        this.serv = serv;
        this.stage = stage;
        textFieldPassword.setText("implicit");
    }

    public void handleLogIn() throws NoSuchAlgorithmException {
        Long id = Long.parseLong(textFieldId.getText());
        String password = textFieldPassword.getText();

        if(serv.findUser(id).isEmpty()){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "Wrong id!");
            return;
        }

        User user = serv.findUser(id).get();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        // Convert the byte array to a hexadecimal string
        StringBuilder hashedPassword = new StringBuilder();
        for (byte b : md.digest()) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hashedPassword.append('0');
            }
            hashedPassword.append(hex);
        }

        if(!Objects.equals(hashedPassword.toString(), user.getPassword())){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "Wrong password!");
            return;
        }

        stage.close();
        showUserWindow(user);

    }

    @FXML
    public void handleSignUp(ActionEvent ev){

        showUserUCDDialog();

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
            userController.setService(serv, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showUserUCDDialog() {
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
            userUCController.setService(serv, dialogStage, null);

            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
