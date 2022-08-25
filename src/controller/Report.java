

package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import misc.ConvertTimeZoneInterface;
import misc.Database;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Report controller. The Time Zone conversion lambda located in the Report method - converts Universal Date Time from the client_schedule database into the User's local Date Time.
*/

public class Report implements Initializable {

    Stage stage;
    Parent scene;

    @FXML Button onActionContactScheduleButton;
    @FXML Button onActionTypeMonthButton;
    @FXML Button onActionReport3;
    @FXML TextArea reportTextArea;

    static ObservableList<String> reports = FXCollections.observableArrayList();

    //Time conversion Lambda
    static ConvertTimeZoneInterface conversion = (String dateTime) -> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    };


    @FXML
    void TypeMonthButton(ActionEvent event) throws SQLException {
        reportTextArea.setText(reportTypeMonth());
    }

    //Get all Contacts
    public static ObservableList<String> getAllContacts() throws SQLException {
        ObservableList<String> allContacts = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement("SELECT DISTINCT Contact_Name FROM contacts");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allContacts.add(resultSet.getString("Contact_Name"));
        }
        preparedStatement.close();
        return allContacts;
    }

    //Get Contact ID's
    public static Integer getContactID (String contactName) throws SQLException, Exception{
        Integer contactID = -1;
        PreparedStatement preparedStatement = Database.connection().prepareStatement(
                "SELECT Contact_ID, Contact_Name FROM contacts WHERE Contact_Name = ?");
        preparedStatement.setString(1, contactName);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            contactID = resultSet.getInt("Contact_ID");
        }
        preparedStatement.close();
        return contactID;
    }

    //Get Contacts appointments as strings
    public static ObservableList<String> getContactAppointmentStrings (String contactID) throws SQLException {
        ObservableList<String> appointmentContactString = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement(
                "SELECT * FROM appointments WHERE Contact_ID = ?");

        preparedStatement.setString(1, contactID);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String appointmentID = resultSet.getString("Appointment_ID");
            String title = resultSet.getString("Title");
            String type = resultSet.getString("Type");
            String description = resultSet.getString("Description");
            LocalDateTime start = conversion.stringLocalDateTime(resultSet.getString("Start"));
            LocalDateTime end = conversion.stringLocalDateTime(resultSet.getString("End"));
            String customerID = resultSet.getString("Customer_ID");


            String line = "     Appointment ID: " + appointmentID + "    Title: " + title + "    Type: " + type + "\n";
            line += "       Description: " + description + "\n";
            line += "       Start: " + start + "   End: " + end + "\n";
            line += "       Customer ID: " + customerID + "\n";
            line += "\n";

            appointmentContactString.add(line);
        }

        preparedStatement.close();
        return appointmentContactString;

    }

    //Build Contact report - include: Appointment ID, Title, Type, Description, Start, End, Customer ID
    @FXML
    void ContactScheduleButton(ActionEvent event) throws Exception {

        reportTextArea.clear();

        ObservableList<String> listContacts = getAllContacts();

        for (String contact : listContacts) {
            String contactID = getContactID(contact).toString();
            reportTextArea.appendText("Contact Name:    " + contact + "     ID: " + contactID + "\n");

            ObservableList<String> appointments = getContactAppointmentStrings(contactID);
            if (appointments.isEmpty()) {
                reportTextArea.appendText("Contact has no appointments");
            }
            for (String appointment : appointments) {
                reportTextArea.appendText(appointment);
            }

        }
    }

    //Generate report w/ total number of appointment per date sorted by date
    @FXML
    void ReportButton(ActionEvent event) throws SQLException {
        StringBuilder reportByDate = new StringBuilder();
        PreparedStatement countStatment = Database.connection().prepareStatement(
                "SELECT CAST(Start AS DATE) AS \"dateCount\", COUNT(*) AS \"totalPerDate\"\n" +
                        "FROM client_schedule.appointments\n" +
                        "GROUP BY (CAST(Start AS DATE))\n" +
                        "ORDER BY CAST(Start AS DATE)");
        ResultSet countResults = countStatment.executeQuery();
        while (countResults.next()) {
            String countString = "Date: " + countResults.getString("dateCount") + "  " + "Total Appointments: " + countResults.getString("totalPerDate") + "\n";
            reportByDate.append(countString);
        }
        countStatment.close();
        reportTextArea.setText(String.valueOf(reportByDate));
    }

    //Cancel & Return to Main Screen
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    //Generate report w/ the total number of customer appointments by type & month
    public String reportTypeMonth() throws SQLException {
        try {
            StringBuilder reportTypeMonthString = new StringBuilder();

            PreparedStatement typeStatement = Database.connection().prepareStatement(
                    "SELECT Type, COUNT(Type) as \"Total\"FROM appointments GROUP BY Type");
            PreparedStatement monthStatement = Database.connection().prepareStatement(
                    "SELECT MONTHNAME(Start) as \"Month\", COUNT(MONTH(Start)) as \"Total\" from appointments GROUP BY Month");
            ResultSet resultsType = typeStatement.executeQuery();
            ResultSet resultsMonth = monthStatement.executeQuery();

            while (resultsType.next()) {
                String stringType = "Appointment Type: " + resultsType.getString("Type") + "\n" +
                        "   Total: " + resultsType.getString("Total") + "\n" + "\n";
                reportTypeMonthString.append(stringType);
            }
            while (resultsMonth.next()) {
                String stringMonth = "Appointment Month: " + resultsMonth.getString("Month") + "\n" +
                       "    Total: " + resultsMonth.getString("Total") + "\n";
                reportTypeMonthString.append(stringMonth);

                return reportTypeMonthString.toString();
            }
        } catch (SQLException e) {
            System.out.println("Error" + e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

