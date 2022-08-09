/**
 * Appointment Add controller
 */

package controller;

import javafx.collections.ObservableList;
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
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class AppointmentAdd implements Initializable {

    @FXML private DatePicker pickAppointmentDate;
    @FXML private TextField AppointmentTitleText;
    @FXML private TextArea AppointmentDescriptionText;
    @FXML private TextField AppointmentLocationText;
    @FXML private TextField AppointmentTypeText;
    @FXML private ComboBox endTimeCombo;
    @FXML private ComboBox startTimeCombo;
    @FXML private ComboBox CustomerIDCombo;
    @FXML private ComboBox UserIDCombo;
    @FXML private ComboBox ContactIDCombo;
    @FXML private Button appointmentCancelButton;
    @FXML private Button appointmentSaveButton;

    Stage stage;
    Parent scene;


    @FXML
    public void onActionSave(ActionEvent event) throws IOException {
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

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
