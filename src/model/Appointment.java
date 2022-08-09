/**
 * Appointment Model
 */

package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import misc.Database;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Appointment {

    String id, title, description, location, type, start, end, createDate, createBy, lastUpdateDate, lastUpdateBy, customerID, userID, contactID;

    public Appointment() {
    }

    //SHORT - For Main Table View
    public Appointment(String id, String title, String description, String location, String type,
                       String start, String end, String customerID, String userID, String contactID) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    //FULL - For adding to Database - includes everything + create & last update
    public Appointment(String id, String title, String description, String location, String type,
                       String start, String end, String createDate, String createBy, String lastUpdateDate,
                       String lastUpdateBy, String customerID, String userID, String contactID) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createBy = createBy;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdateBy = lastUpdateBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }



    //Getters/setters
    public String getId() {return id;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public String getLocation() {return location;}
    public String getType() {return type;}
    public String getStart() {return start;}
    public String getEnd() {return end;}
    public String getCustomerID() {return customerID;}
    public String getUserID() {return userID;}
    public String getContactID() {return contactID;}
    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setLocation(String location) {this.location = location;}
    public void setType(String type) {this.type = type;}
    public void setStart(String start) {this.start = start;}
    public void setEnd(String end) {this.end = end;}
    public void setCustomerID(String customerID) {this.customerID = customerID;}
    public void setUserID(String userID) {this.userID = userID;}
    public void setContactID(String contactID) {this.contactID = contactID;}

    static ObservableList<String> timeCombo = FXCollections.observableArrayList();

    //Appointment in 15 minutes
    public static boolean appointmentFifteen () {
        try {
            ResultSet appointmentSoon = Database.connection().createStatement().executeQuery(String.format(
                    "SELECT Appointment_ID, Start\n " +
                    "FROM appointments\n" +
                    "WHERE Appointment_ID = '%s' AND Start BETWEEN '%s' AND '%s'",
                    User.getPresentUser(), LocalDateTime.now(ZoneId.of("UTC")), LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15)));
                    appointmentSoon.next();

            String appointmentInfo = appointmentSoon.getString("Appointment_Name" + " " + "Start");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Upcoming Appointment");
            alert.setContentText("Upcoming Appointment: " + appointmentInfo);
            alert.showAndWait();

            return true;
        } catch (SQLException e) {
            System.out.println("No upcoming appointments.");
            return false;
        }
    }


    //Add Time to Start/End Combo Boxes
    public static ObservableList<String> getTimeCombo() {
        try {
            timeCombo.removeAll(timeCombo);
            for (int i = 0; i < 24; i++ ) {
                String hour;
                if(i < 10) {
                    hour = "0" + i;
                }

                else {
                    hour = Integer.toString(i);
                }
                timeCombo.add(hour + ":00:00");
                timeCombo.add(hour + ":15:00");
                timeCombo.add(hour + ":30:00");
                timeCombo.add(hour + ":45:00");
            }
            timeCombo.add("24:00:00");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return timeCombo;
    }

    //Get all CustomerID
    public static ObservableList<String> getAllCustomerID() throws SQLException {
        ObservableList<String> allCustomerID = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement("SELECT DISTINCT Customer_ID FROM customers");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allCustomerID.add(resultSet.getString("Customer_ID"));
        }
        preparedStatement.close();
        return allCustomerID;
    }

    //Get all UserID
    public static ObservableList<String> getAllUserID() throws SQLException {
        ObservableList<String> allUserID = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement("SELECT DISTINCT User_ID FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allUserID.add(resultSet.getString("User_ID"));
        }
        preparedStatement.close();
        return allUserID;
    }

    //Get all ContactID
    public static ObservableList<String> getAllContactID() throws SQLException {
        ObservableList<String> allContactID = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement("SELECT DISTINCT Contact_ID FROM contacts");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allContactID.add(resultSet.getString("Contact_ID"));
        }
        preparedStatement.close();
        return allContactID;
    }

    //Add Appointment to Database
    public static void addAppointment(String date, String title, String description, String location, String type, String start, String end, String customerID, String userID, String contactID) throws SQLException {
        //Convert to format: "YYYY-MM-DD 00:00:00"
        LocalDateTime localStart = convertUTCString(start, date);
        LocalDateTime localEnd = convertUTCString(end, date);
        String startUTC = localStart.toString();
        String endUTC = localEnd.toString();

        //Print out converted time
        System.out.println("UTC Start Date/Time: " + startUTC);
        System.out.println("UTC End Date/Time: " + endUTC);

        //Insert all data into appointments table
        Database.connection.createStatement().executeUpdate(String.format(
                "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", title, description, location, type, startUTC, endUTC, LocalDateTime.now(), User.getPresentUser(), LocalDateTime.now(), User.getPresentUser(), customerID,userID, contactID));

    }

    //User's local date time convert to UTC
    public static LocalDateTime convertUTCString (String time, String date) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt =  LocalDateTime.parse(date + " " + time, format).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            return ldt;
        }

    //Check that appointment is inside business hours - 8AM - 10 PM including weekends
    public static boolean businessHoursCheck (String startTime, String endTime, String date) {
        /**
         * Time Chart Business Hours:
         * Alaska: 4:00 AM | 6:00 PM
         * Business/EST: 8:00 AM - 10:00 PM
         * UTC: 12:00 PM NOON | 2:00 AM
         */
        //Convert start & end to UTC
        LocalDateTime startLocal = convertUTCString(startTime, date);
        LocalDateTime endLocal = convertUTCString(endTime, date);
        String startUTC = startLocal.toString().substring(11,16);
        String endUTC = endLocal.toString().substring(11,16);

        //Compare Business Hours
        LocalTime compareStart = LocalTime.parse(startUTC);
        LocalTime compareEnd = LocalTime.parse(endUTC);
        LocalTime openHour = LocalTime.parse("03:59");
        LocalTime closeHour = LocalTime.parse("18:01");
        Boolean startTimeApprove = compareStart.isAfter(openHour);
        Boolean endTimeApprove = compareEnd.isBefore(closeHour);

        if (startTimeApprove && endTimeApprove) {
            return true;
        }
        else {
            return false;
        }
    }

    //Make sure no appointments are overlapping
    public static boolean checkOverlap (String startTime, String endTime, String date) throws SQLException {
        try {
            //Convert to format: "YYYY-MM-DD 00:00:00"
            LocalDateTime localStart = convertUTCString(startTime, date);
            LocalDateTime localEnd = convertUTCString(endTime, date);
            String startUTC = localStart.toString();
            String endUTC = localEnd.toString();

            ResultSet getDatabaseOverlap = Database.connection().createStatement().executeQuery(String.format(
                            "SELECT Start, End, Customer_Name\n" +
                            "FROM appointments\n" +
                            "INNER JOIN customers ON appointments.Customer_ID = customers.Customer_ID\n" +
                            "WHERE ('%s' >= Start AND '%s' <= End)\n" +
                            "OR ('%s' <= Start AND '%s' >= End)\n" +
                            "OR ('%s' <= Start AND '%s' >= Start)\n" +
                            "OR ('%s' <= End AND '%s' >= End)",
                            startUTC, startUTC, endUTC, endUTC, startUTC, endUTC, startUTC, endUTC));
            getDatabaseOverlap.next();
            System.out.println("Appointment Overlap Occurs: " + getDatabaseOverlap.getString("Customer_Name"));
            return false;
        } catch (SQLException e) {
            System.out.println("No conflicts exist.");
            return true;
        }
    }
}
