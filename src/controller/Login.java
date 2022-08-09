/**
 * Login controller
 */

package controller;

import javafx.scene.control.*;
import misc.Log;
import misc.TimeZoneInterface;
import model.Appointment;
import model.User;
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
import java.time.ZoneId;
import java.util.Locale;
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
    @FXML private Label UserLocationLabel;

    //Define alerts
    private String alertTitle;
    private String alertHeader;
    private String alertContent;

    //Login
    public void onActionLogin(ActionEvent event) throws IOException {


        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean verifiedUser = User.login(username, password);
        if(verifiedUser == true) {

            //Log successful login
            Log.logEvent(username, true, "Login");
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();

            if (Appointment.appointmentFifteen()) {
                System.out.println("Appointment in 15.");
            }
            else {
                System.out.println("No appointment in the next 15 minutes.");
            }
        }
        else {
            //Log unsuccessful login
            Log.logEvent(username, false, "Login");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTitle);
            alert.setHeaderText(alertHeader);
            alert.setContentText(alertContent);
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

        //Display user's timezone (using Lambda expression)
        TimeZoneInterface display = () -> "Time Zone: " + ZoneId.systemDefault().toString();
        UserLocationLabel.setText(display.getUserTimeZone());

        Locale locale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("misc/LanguageResourceBundle", locale);
        ScheduleLoginTitle.setText(resourceBundle.getString("ScheduleLogin"));
        UsernameLabel.setText(resourceBundle.getString("Username"));
        PasswordLabel.setText(resourceBundle.getString("Password"));
        loginButton.setText(resourceBundle.getString("login"));
        resetButton.setText(resourceBundle.getString("reset"));
        exitButton.setText(resourceBundle.getString("exit"));
        alertTitle = resourceBundle.getString("alertTitle");
        alertHeader = resourceBundle.getString("alertHeader");
        alertContent = resourceBundle.getString("alertContent");

    }
}