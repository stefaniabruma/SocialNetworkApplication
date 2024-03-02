package ir.map.socialnetworkapp;

import ir.map.socialnetworkapp.Domain.User;
import ir.map.socialnetworkapp.Service.Service;
import ir.map.socialnetworkapp.Utils.GUIMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UpdateCredentialsController {

    Service serv;
    Stage stage;
    User user;

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldNewPass;

    @FXML
    private TextField textFieldConfirmPass;

    public void setService(Service serv, Stage stage,User user){
        this.serv = serv;
        this.stage = stage;
        this.user = user;

        initFields();
    }

    private void initFields() {
        textFieldId.setText(user.getId().toString());

        textFieldId.setEditable(false);
    }

    @FXML
    private void handleConfirmButton() throws NoSuchAlgorithmException {

        if(!textFieldNewPass.getText().equals(textFieldConfirmPass.getText())){
            GUIMessage.showMessage(null, Alert.AlertType.ERROR, "Error", "Passwords dont match!");
            stage.close();
            return;
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(textFieldNewPass.getText().getBytes());

        StringBuilder hashedPassword = new StringBuilder();
        for (byte b : md.digest()) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hashedPassword.append('0');
            }
            hashedPassword.append(hex);
        }

        serv.updateUser(user.getId(), user.getFirstName(), user.getLastName(), hashedPassword.toString());

        stage.close();
    }


}
