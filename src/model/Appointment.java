

package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import misc.ConvertTimeZoneInterface;
import misc.Database;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Appointment Model The Time Zone conversion lambda located in the Report method - converts Universal Date Time from the client_schedule database into the User's local Date Time.
 */

public class Appointment {

    String id, title, description, location, type, date, start, end, createDate, createBy, lastUpdateDate, lastUpdateBy, customerID, userID, contactID;

    public Appointment() {
    }

    //SHORT - For Main Table View
    public Appointment(String id, String title, String description, String location, String type, String date,
                       String start, String end, String customerID, String userID, String contactID) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    //FULL - For adding to Database - includes everything + create & last update
    public Appointment(String id, String title, String description, String location, String type, String date,
                       String start, String end, String createDate, String createBy, String lastUpdateDate,
                       String lastUpdateBy, String customerID, String userID, String contactID) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
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
    public String getDate() {return date;}
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
    public void setDate(String date) {this.date = date;}
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

    //Time conversion Lambda
    static ConvertTimeZoneInterface conversion = (String dateTime) -> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    };

    //Appointment in 15 minutes
    public static boolean appointmentFifteen() {

        Integer userInt = User.getPresentUser().getUserID();
        System.out.println("User ID: " + userInt);

        StringBuilder upcomingAppointmentsStringBuild = new StringBuilder();

        LocalDateTime newNow = LocalDateTime.now();
        ZoneId zoneID = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = newNow.atZone(zoneID);
        LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        System.out.println("Start: " + localDateTime);
        LocalDateTime localDateTime15 = localDateTime.plusMinutes(15);

        try {
            Statement statement = Database.connection().createStatement();
            String query = "SELECT * FROM appointments WHERE Start BETWEEN '" + localDateTime + "' AND ' " + localDateTime15 + "' AND " + "User_ID='" + userInt + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String appointmentsUpcoming = "Appointment ID: " + resultSet.getString("Appointment_ID") + " " + "Date/Time: " + conversion.stringLocalDateTime(resultSet.getString("Start"));
                upcomingAppointmentsStringBuild.append(appointmentsUpcoming);
                System.out.println(upcomingAppointmentsStringBuild);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Upcoming Appointments");
                alert.setHeaderText("Upcoming Appointment: " + upcomingAppointmentsStringBuild);
                alert.showAndWait();
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        System.out.println("False");
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Upcoming Appointments");
        alert.setHeaderText("No upcoming appointments in the next 15 minutes.");
        alert.showAndWait();
        return false;
    }



    private static void appointmentAlert(String name) {
    }


    //Add Time to Start/End Combo Boxes
    public static ObservableList<String> getTimeCombo() {
        try {
            timeCombo.clear();
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

    //Update Appointment
    public static Boolean updateAppointment(String title, String description, String location, String type, String date,
                                         String start, String end, String customerID, String userID, String contactID,
                                         String id) throws SQLException {

        //Change start + date to YYYY-MM-DD 00:00:00 / Local to UTC for Database
        LocalDateTime startLocal = convertUTCString(start, date);
        LocalDateTime endLocal = convertUTCString(end,date);
        String startUTC = startLocal.toString();
        String endUTC = endLocal.toString();

        //Insert all data into appointments table
        PreparedStatement preparedStatement = Database.connection().prepareStatement(
                "UPDATE appointments\n" +
                     "SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=?, Last_Updated_By=?,\n" +
                        "Customer_ID=?, User_ID=?, Contact_ID=?\n" +
                     "WHERE Appointment_ID =?");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, location);
            preparedStatement.setString(4, type);
            preparedStatement.setString(5, startUTC);
            preparedStatement.setString(6, endUTC);
            preparedStatement.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
            preparedStatement.setString(8, User.getPresentUser().getUsername());
            preparedStatement.setString(9, customerID);
            preparedStatement.setString(10, userID);
            preparedStatement.setString(11, contactID);
            preparedStatement.setString(12, id);

            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            }
            catch (SQLException e){
                e.printStackTrace();
                return false;
            }
    }

    //Delete Appointment
    public static Boolean deleteAppointment (String id) throws SQLException {
        PreparedStatement preparedStatement = Database.connection().prepareStatement("DELETE FROM appointments " +
                "WHERE Appointment_ID = ?");
            preparedStatement.setInt(1, Integer.parseInt(id));
            try {
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    //User's local date time convert to EST
    public static LocalDateTime convertESTString (String time, String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt =  LocalDateTime.parse(date + " " + time, format).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York")).toLocalDateTime();
        return ldt;
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
         * Business/EST: 8:00 AM - 10:00 PM -Includes weekends
         * UTC: 12:00 PM NOON | 2:00 AM
         */
        //Convert start & end to UTC
        LocalDateTime startLocal = convertESTString(startTime, date);
        System.out.println("Start Local: " + startLocal);
        LocalDateTime endLocal = convertESTString(endTime, date);
        System.out.println("End Local: " + endLocal);
        String startUTC = startLocal.toString().substring(11,16);
        System.out.println("Start EST: " + startUTC);
        String endUTC = endLocal.toString().substring(11,16);
        System.out.println("End EST: " + endUTC);

        //Compare Business Hours
        LocalTime compareStart = LocalTime.parse(startUTC);
        System.out.println("Compare Start: " + compareStart);
        LocalTime compareEnd = LocalTime.parse(endUTC);
        System.out.println("Compare End: " + compareEnd);
        //New York/East Coast Time
        LocalTime openHour = LocalTime.parse("07:59");
        System.out.println("Open Hour: " + openHour);
        LocalTime closeHour = LocalTime.parse("22:01");
        System.out.println("Close Hour: " + closeHour);
        Boolean startTimeApprove = compareStart.isAfter(openHour);
        System.out.println("Start Time Approve: " + startTimeApprove);
        Boolean endTimeApprove = compareEnd.isBefore(closeHour);
        System.out.println("End Time Approve: " + endTimeApprove);


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
                            "SELECT Start, End, Customer_Name, Appointment_ID\n" +
                            "FROM appointments\n" +
                            "INNER JOIN customers ON appointments.Customer_ID = customers.Customer_ID\n" +
                            "WHERE ('%s' >= Start AND '%s' <= End)\n" +
                            "OR ('%s' <= Start AND '%s' >= End)\n" +
                            "OR ('%s' <= Start AND '%s' >= Start)\n" +
                            "OR ('%s' <= End AND '%s' >= End)",
                            startUTC, startUTC, endUTC, endUTC, startUTC, endUTC, startUTC, endUTC));
            getDatabaseOverlap.next();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Appointment could not be scheduled because overlap occurs with Appointment_ID: " + getDatabaseOverlap.getString("Appointment_ID") +
                    "   Customer Name: " + getDatabaseOverlap.getString("Customer_Name"));
            alert.showAndWait();
            return false;
        } catch (SQLException e) {
            System.out.println("No conflicts exist.");
            return true;
        }
    }

    public static boolean checkOverlapSelected (String startTime, String endTime, String date, String customerID, String id) throws SQLException {
        try {
            //Convert to format: "YYYY-MM-DD 00:00:00"
            LocalDateTime localStart = convertUTCString(startTime, date);
            LocalDateTime localEnd = convertUTCString(endTime, date);
            String startUTC = localStart.toString();
            String endUTC = localEnd.toString();

            ResultSet getDatabaseOverlap = Database.connection().createStatement().executeQuery(String.format(
                    "SELECT Start, End, Customer_ID, Appointment_ID\n" +
                            "FROM appointments\n" +
                            "WHERE ('%s' >= Start AND '%s' <= End)\n" +
                            "OR ('%s' <= Start AND '%s' >= End)\n" +
                            "OR ('%s' <= Start AND '%s' >= Start)\n" +
                            "OR ('%s' <= End AND '%s' >= End)",
                            startUTC, startUTC, endUTC, endUTC, startUTC, endUTC, startUTC, endUTC));
            getDatabaseOverlap.next();
            System.out.println("Appointment Overlap Occurs: " + "Customer ID" + getDatabaseOverlap.getString("Customer_ID"));

            String startCheck = getDatabaseOverlap.getString("Start").substring(0, 16);
            String endCheck = getDatabaseOverlap.getString("End").substring(0, 16);


                if (getDatabaseOverlap.getString("Customer_ID").equals(customerID) && getDatabaseOverlap.getString("Appointment_ID").equals(id)) {
                    System.out.println("Conflicts with current appointment. Current: " + getDatabaseOverlap.getString("Customer_ID") + "Appointment ID: " + getDatabaseOverlap.getString("Appointment_ID"));
                    return true;
                }
                else{
                    System.out.println("Check next");
                    System.out.println(getDatabaseOverlap.getString("Customer_ID: ") + " " + customerID + getDatabaseOverlap.getString("Appointment_ID") + id + startCheck + endCheck);
                    return false;
                    }
        } catch (SQLException e) {
            return true;
        }
    }

}
