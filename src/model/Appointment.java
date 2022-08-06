/**
 * Appointment Model
 */

package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misc.Database;

import java.sql.*;

public class Appointment {

    private Integer id;
    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private Integer customerID;
    private Integer userID;
    private Integer contactID;

    public Appointment () {}

    public Appointment (Integer textId, String textTitle, String textDescription, String textLocation, String textType,
                        Timestamp textStart, Timestamp textEnd, Integer textCustomerID, Integer textUserID, Integer textContactID) {

        id = textId;
        title = textTitle;
        description = textDescription;
        location = textLocation;
        type = textType;
        start = textStart;
        end = textEnd;
        customerID = textCustomerID;
        userID = textUserID;
        contactID = textContactID;

    }

    //Getters/setters
    public Integer getId(){return id;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public String getLocation() {return location;}
    public String getType() {return type;}
    public Timestamp getStart() {return start;}
    public Timestamp getEnd() {return end;}
    public Integer getCustomerID() {return customerID;}
    public Integer getUserID() {return userID;}
    public Integer getContactID() {return contactID;}
    public void setId(Integer id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setLocation(String location) {this.location = location;}
    public void setType(String type) {this.type = type;}
    public void setStart(Timestamp start) {this.start = start;}
    public void setEnd(Timestamp end) {this.end = end;}
    public void setCustomerID(Integer customerID) {this.customerID = customerID;}
    public void setUserID(Integer userID) {this.userID = userID;}
    public void setContactID(Integer contactID) {this.contactID = contactID;}

    //Get All appointments observable list
    public static ObservableList<Appointment> getAllAppointments() throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement(
                "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, " +
                        "customers.Customer_ID, users.User_ID, contacts.Contact_ID  " +
                     "FROM client_schedule.appointments\n" +
                     "INNER JOIN client_schedule.customers ON appointments.Customer_ID = customers.Customer_ID \n" +
                     "INNER JOIN client_schedule.users ON appointments.User_ID = users.User_ID\n" +
                     "INNER JOIN client_schedule.contacts ON appointments.Contact_ID = contacts.Contact_ID\n ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Integer id = resultSet.getInt("Appointment_ID");
            String title = resultSet.getString("Title");
            String description = resultSet.getString("Description");
            String  location = resultSet.getString("Location");
            String type = resultSet.getString("Type");
            Timestamp startTime = resultSet.getTimestamp("Start");
            Timestamp endTime = resultSet.getTimestamp("End");
            Integer customerID = resultSet.getInt("Customer_ID");
            Integer userID = resultSet.getInt("User_ID");
            Integer contactID = resultSet.getInt("Contact_ID");

        Appointment newAppointment = new Appointment(id, title, description, location, type, startTime, endTime, customerID, userID, contactID);

        allAppointments.add(newAppointment);
        }
        Database.closeConnection();
        return allAppointments;
    }




}
