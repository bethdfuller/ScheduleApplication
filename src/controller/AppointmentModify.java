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
    void onActionSave(ActionEvent event) throws IOException {
        try {
            //Collect information user has entered
            String appointmentDate = pickAppointmentDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String title = AppointmentTitleText.getText();
            String description = AppointmentDescriptionText.getText();
            String location = AppointmentLocationText.getText();
            String type = AppointmentTypeText.getText();

            int startTime = startTimeCombo.getSelectionModel().getSelectedIndex();
            int endTime = endTimeCombo.getSelectionModel().getSelectedIndex();
            String startTimeString = (String) startTimeCombo.getValue().toString();
            String endTimeString = (String) endTimeCombo.getValue().toString();

            String customerId = CustomerIDCombo.getValue().toString();
            String userID = UserIDCombo.getValue().toString();
            String contactID = ContactIDCombo.getValue().toString();

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
                        if (Appointment.checkOverlap(startTimeString, endTimeString, appointmentDate) && Appointment.businessHoursCheck(startTimeString, endTimeString, appointmentDate)) {
                            try {
                                Appointment.addAppointment(appointmentDate, title, description, location, type, startTimeString, endTimeString, customerId, userID, contactID);
                                System.out.println("Appointment added to database.");
                                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                                stage.setScene(new Scene(scene));
                                stage.show();

                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        else {
                            if (!Appointment.checkOverlap(startTimeString, endTimeString, appointmentDate)) {
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

        //Takes Date from Start to use for Date Picker
        String dateString = AppointmentToModify.getStart();
        LocalDateTime localDateTime = LocalDateTime.parse(dateString);
        pickAppointmentDate.setValue(LocalDate.from(localDateTime));

        //Load selected appointment data into the Modify Appointment Screen
        AppointmentIDLabel.setText(String.valueOf(AppointmentToModify.getId()));
        AppointmentTitleText.setText(AppointmentToModify.getTitle());
        AppointmentDescriptionText.setText(AppointmentToModify.getDescription());
        AppointmentLocationText.setText(AppointmentToModify.getLocation());
        AppointmentTypeText.setText(AppointmentToModify.getType());
        startTimeCombo.getValue();
        endTimeCombo.getValue();
        CustomerIDCombo.getValue();
        UserIDCombo.getValue();
        ContactIDCombo.getValue();

        //Time Zone conversion lambda
        ConvertTimeZoneInterface conversion = (String dateTime) -> {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return ldt;
        };

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
