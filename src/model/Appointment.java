/**
 * Appointment Model
 */

package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misc.Database;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

    String id, title, description, location, type, start, end, customerID, userID, contactID;

    public Appointment () {}

    public Appointment (String textId, String textTitle, String textDescription, String textLocation, String textType,
                        String textStart, String textEnd, String textCustomerID, String textUserID, String textContactID) {

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
    public String getId(){return id;}
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



}
