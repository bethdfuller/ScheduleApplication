/**
 * Report controller
 */

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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import misc.ConvertTimeZoneInterface;
import misc.Database;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Report implements Initializable {

    Stage stage;
    Parent scene;

    @FXML Button onActionContactScheduleButton;
    @FXML Button onActionTypeMonthButton;
    @FXML Button onActionReport3;
    @FXML TextArea reportTextArea;

    static ObservableList<String> reports = FXCollections.observableArrayList();

    //Time Zone conversion lambda
    ConvertTimeZoneInterface conversion = (String dateTime) -> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    };


    @FXML
    void TypeMonthButton(ActionEvent event) throws SQLException {
        reportTextArea.setText(reportTypeMonth());
    }

    //Build Contact report - include: Appointment ID, Title, Type, Description, Start, End, Customer ID
    @FXML
    void ContactScheduleButton(ActionEvent event) throws SQLException {

        StringBuilder reportContactScheduleString = new StringBuilder();

        String query = "SELECT Contact_Name, contacts.Contact_ID, Appointment_ID, Title, Type, Description, Start, End, Customer_ID\n" +
                "FROM appointments\n" +
                "JOIN contacts on contacts.Contact_ID = appointments.Contact_ID\n" +
                "WHERE start>=NOW()\n" +
                "ORDER BY Contact_Name, start";

        Statement statement = Database.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuffer contactScheduleString = new StringBuffer();

        while (resultSet.next()) {
            //Sort appointments by Contact
            String contactNameString = "Contact Name: " + resultSet.getString("Contact_Name") + "   " + "Contact ID: " + resultSet.getString("Contact_ID") + "\n";
            reportContactScheduleString.append(contactNameString);
            //Appointment information lines
            while (resultSet.next()) {
                String appointmentIDString = "Appointment_ID: " + resultSet.getString("Appointment_ID") + "    " + "Title: " + resultSet.getString("Title") + "    " +
                        "Type: " + resultSet.getString("Type") + "  " + "Description: " + resultSet.getString("Description")+ "    " +
                        "Start: " + resultSet.getString("Start") + "    " + "End: " + resultSet.getString("End") + "    " + "Customer ID: " + resultSet.getString("Customer_ID") + "\n";
                reportContactScheduleString.append(appointmentIDString);
            }
            statement.close();
            reportTextArea.setText(String.valueOf(reportContactScheduleString));
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
                String stringType = "Appointment Type: " + resultsType.getString("Type") + "    " +
                        "Total: " + resultsType.getString("Total") + "\n";
                reportTypeMonthString.append(stringType);
            }
            while (resultsMonth.next()) {
                String stringMonth = "Appointment Month: " + resultsMonth.getString("Month") + "///" + "    " +
                        resultsMonth.getString("Total") + "\n";
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

