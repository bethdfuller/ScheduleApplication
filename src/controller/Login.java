package controller;

import javafx.scene.control.*;
import misc.Log;
import model.UserDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class Login implements Initializable {

    Stage stage;
    Parent scene;

    //Login parts
    @FXML Label ScheduleLoginTitle;
    @FXML Label UsernameLabel;
    @FXML TextField usernameField;
    @FXML Label PasswordLabel;
    @FXML PasswordField passwordField;
    @FXML Button loginButton;
    @FXML Button resetButton;
    @FXML Button exitButton;

    //Login
    public void onActionLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean verifiedUser = UserDatabase.login(username, password);
        if(verifiedUser == true) {

            //Log successful login
            Log.logEvent(username, true, "Login");


            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        else {
            //Log unsuccessful login
            Log.logEvent(username, false, "Login");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid username/password input.");
            alert.showAndWait();

        }
    }

    //Reset fields
    @FXML void onActionReset(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
    }

    //Exit Application
    @FXML
    void onActionExit(ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
