/**
 * Appointment Modify controller
 */

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.ConvertTimeZoneInterface;
import misc.TimeZoneInterface;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AppointmentModify implements Initializable {

    Stage stage;
    Parent scene;

    @FXML DatePicker pickAppointmentDate;
    @FXML private Label AppointmentIDLabel;
    @FXML TextField AppointmentTitleText;
    @FXML TextArea AppointmentDescriptionText;
    @FXML TextField AppointmentLocationText;
    @FXML TextField AppointmentTypeText;
    @FXML ComboBox <String> startTimeCombo;
    @FXML ComboBox <String> endTimeCombo;
    @FXML ComboBox <String> CustomerIDCombo;
    @FXML ComboBox <String> UserIDCombo;
    @FXML ComboBox <String> ContactIDCombo;
    @FXML Button appointmentCancelButton;
    @FXML Button appointmentSaveButton;

    private Appointment AppointmentToModify;

    //Save Modified Appointment
    @FXML
    void onActionSave(ActionEvent event) {
        try {
            //Collect information user has entered
            String appointmentDate = pickAppointmentDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String id = AppointmentIDLabel.getText();
            String title = AppointmentTitleText.getText();
            String description = AppointmentDescriptionText.getText();
            String location = AppointmentLocationText.getText();
            String type = AppointmentTypeText.getText();

            int startTime = startTimeCombo.getSelectionModel().getSelectedIndex();
            int endTime = endTimeCombo.getSelectionModel().getSelectedIndex();

            String startTimeString = startTimeCombo.getValue();
            String endTimeString = endTimeCombo.getValue();

            String customerId = CustomerIDCombo.getValue();
            String userID = UserIDCombo.getValue();
            String contactID = ContactIDCombo.getValue();

            try {
                if (appointmentDate.isEmpty() || title.isEmpty() || description.isEmpty() || location.isEmpty() ||
                        type.isEmpty() || startTimeString.isEmpty() || endTimeString.isEmpty() || customerId.isEmpty()
                        || userID.isEmpty() || contactID.isEmpty()) ;
                else {
                    //Start time must be before end time
                    if (endTime < startTime) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setContentText("Start time must be before end time.");
                        alert.showAndWait();
                    }
                    //Start and end time must be different
                    else if (startTime == endTime) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setContentText("Start and end time cannot be the same.");
                        alert.showAndWait();
                    } else {
                        if (Appointment.checkOverlapSelected(startTimeString, endTimeString, appointmentDate, customerId, id) && Appointment.businessHoursCheck(startTimeString, endTimeString, appointmentDate)) {
                            Appointment.updateAppointment(title, description, location, type, appointmentDate, startTimeString, endTimeString, customerId, userID, contactID, id);
                            ButtonType clickOK = new ButtonType("Successful/Main Screen", ButtonBar.ButtonData.OK_DONE);
                            Alert emptyField = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has been successfully updated.", clickOK);
                            emptyField.showAndWait();
                            //Return to Main Screen after successful customer Add
                            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        }
                        else {
                            if (!Appointment.checkOverlapSelected(startTimeString, endTimeString, appointmentDate, customerId, id)) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Warning");
                                alert.setContentText("Appointment could not be scheduled because it overlaps with an existing appointment.");
                                alert.showAndWait();
                            }
                            if (!Appointment.businessHoursCheck(startTimeString, endTimeString, appointmentDate)) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Warning");
                                alert.setContentText("Appointment could not be scheduled because it is not during business hours. Business hours are 8:00 AM - 10:00 PM EST");
                                alert.showAndWait();
                            }
                        }
                    }
                }
            } catch (IOException | SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            ButtonType clickOK = new ButtonType("Understand", ButtonBar.ButtonData.OK_DONE);
            Alert emptyField = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has been not been added to the database - check fields.", clickOK);
            emptyField.showAndWait();
        }
    }

    //Cancel & return to main screen
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Load appointment
        AppointmentToModify = Main.getAppointmentToModify();

        //Load selected appointment data into the Modify Appointment Screen
        AppointmentIDLabel.setText(String.valueOf(AppointmentToModify.getId()));
        AppointmentTitleText.setText(AppointmentToModify.getTitle());
        AppointmentDescriptionText.setText(AppointmentToModify.getDescription());
        AppointmentLocationText.setText(AppointmentToModify.getLocation());
        AppointmentTypeText.setText(AppointmentToModify.getType());
        pickAppointmentDate.setValue(LocalDate.parse(AppointmentToModify.getDate()));
        startTimeCombo.getSelectionModel().select(AppointmentToModify.getStart().substring(11,16) + ":00");
        endTimeCombo.getSelectionModel().select(AppointmentToModify.getEnd().substring(11,16) + ":00");
        CustomerIDCombo.getSelectionModel().select(AppointmentToModify.getCustomerID());
        UserIDCombo.getSelectionModel().select(AppointmentToModify.getUserID());
        ContactIDCombo.getSelectionModel().select(AppointmentToModify.getContactID());

        //Add times to start/end combo boxes
        startTimeCombo.setItems(Appointment.getTimeCombo());
        endTimeCombo.setItems(Appointment.getTimeCombo());

        //Populate Customer ID Combo box
        try {
            CustomerIDCombo.setItems(Appointment.getAllCustomerID());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Populate User ID Combo box
        try {
            UserIDCombo.setItems(Appointment.getAllUserID());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //Populate Contact ID Combo box
        try {
            ContactIDCombo.setItems(Appointment.getAllContactID());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
