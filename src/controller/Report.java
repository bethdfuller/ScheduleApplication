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
    @FXML Button onActionReset;
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

    @FXML
    void ContactScheduleButton(ActionEvent event) throws SQLException {

        StringBuilder contactScheduleString = new StringBuilder();

        String query = "SELECT Appointment_ID, Contact_Name, contacts.Contact_ID, Start\n" +
                        "FROM appointments\n" +
                        "JOIN contacts on contacts.Contact_ID = appointments.Contact_ID\n" +
                        "WHERE start>=NOW()\n" +
                        "ORDER BY Contact_Name, start";

        Statement statement = Database.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuffer string1 = new StringBuffer();
        StringBuffer string2 = new StringBuffer();
        StringBuffer string3 = new StringBuffer();
        StringBuffer string4 = new StringBuffer();

        while (resultSet.next()) {
            string1.append(String.format("%s\n", resultSet.getString("Appointment_ID")));
            string2.append(String.format("%s\n", resultSet.getString("Contact_Name")));
            string3.append(String.format("%s\n", resultSet.getString("Contact_ID")));
            string4.append(String.format("%s\n", resultSet.getString("Start")));


        }
        statement.close();


        reportTextArea.setText(string1.toString());
        reportTextArea.setText(string2.toString());
        reportTextArea.setText(string3.toString());
    }

    @FXML
    void ReportButton(ActionEvent event) {

    }


    public void pressOnActionContactScheduleButton (ActionEvent event) {
    }


    //Reset fields
    @FXML
    void ResetButton(ActionEvent event) throws IOException {
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
        Connection connection = Database.connection();
        try {
            StringBuilder reportTypeMonthString = new StringBuilder();

            PreparedStatement typeStatement = Database.connection().prepareStatement(
                    "SELECT Type, COUNT(Type) as \"Total\"FROM appointments GROUP BY Type");
            PreparedStatement monthStatement = Database.connection().prepareStatement(
                    "SELECT MONTHNAME(Start) as \"Month\", COUNT(MONTH(Start)) as \"Total\" from appointments GROUP BY Month");
            ResultSet resultsType = typeStatement.executeQuery();
            ResultSet resultsMonth = monthStatement.executeQuery();

            while (resultsType.next()) {
                String stringType = "Appointment Type: " + resultsType.getString("Type") + "///" +
                        "Total: " + resultsType.getString("Total") + "\n";
                reportTypeMonthString.append(stringType);
            }
            while (resultsMonth.next()) {
                String stringMonth = "Appointment Month: " + resultsMonth.getString("Month") + "///" + "Total: " +
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

    //Get All Contacts
    public static ObservableList<String> getAllContacts() throws SQLException {
        ObservableList<String> allContacts = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement("SELECT DISTINCT Contact_Name FROM contacts;");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allContacts.add(resultSet.getString("Contact"));
        }
        preparedStatement.close();
        return allContacts;
    }

    //Generate report - Contact Schedule (including: Appointment ID, Title, Type, Description, Start, End Customer ID
    public static ObservableList<String> contactScheduleReport(String contactID) throws SQLException {
        ObservableList<String> contactScheduleList = FXCollections.observableArrayList();
            PreparedStatement preparedStatement = Database.connection().prepareStatement(
                    "SELECT * FROM appointments WHERE Contact_ID = ?");
            preparedStatement.setString(1, contactID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("Appointment_ID");
                String title = resultSet.getString("Title");
                String type = resultSet.getString("Type");
                String start = resultSet.getString("Start");
                String end = resultSet.getString("End");
                String customerID = resultSet.getString("Customer_ID");

                String newLine = "Appointment ID : " + id + "\n";
                newLine += "Title: " + title + "\n";
                newLine += "Type: " + type + "\n";
                newLine += "Start(date/time): " + start + "\n";
                newLine += "End(date/time): " + end + "\n";
                newLine += "Customer ID: " + customerID + "\n";

                contactScheduleList.add(newLine);
            }
            preparedStatement.close();
            return contactScheduleList;
   }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

