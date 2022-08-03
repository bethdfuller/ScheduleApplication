/**
 * Customer Model
 */


package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misc.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class Customer {

    private Integer id;
    private String name;
    private String address;
    private String postalCode;
    private String division;
    private String phone;
    private Integer divisionID;

    public Customer(int id, String name, String address, String division, String postalCode, String phone, Integer divisionID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.division = division;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {this.address = address;}
    public String getDivision() {return division;}
    public void setDivision(String division) {
        this.division = division;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Integer getDivisionID() {return divisionID;}


    //Get all Divisions
    public static ObservableList<String> getAllDivisions() throws SQLException {
        ObservableList<String> allDivisions = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = Database.connection().prepareStatement("SELECT DISTINCT Division FROM first_level_divisions");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allDivisions.add(resultSet.getString("Division"));
        }
        preparedStatement.close();
        return allDivisions;
    }

    //Add Customer to Database
    public static Boolean addCustomer(String name, String address, String postalCode, String phone, Integer divisionID) throws SQLException {

        //Format Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //Insert Customer into SQL Database
        PreparedStatement preparedStatement = Database.connection().prepareStatement(
                "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) \n" +
                        "VALUES(?,?,?,?,?,?,?,?,?);");

        preparedStatement.setString(1, name);
        preparedStatement.setString(2, address);
        preparedStatement.setString(3, postalCode);
        preparedStatement.setString(4, phone);
        preparedStatement.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        preparedStatement.setString(6, User.getPresentUser().getUsername());
        preparedStatement.setString(7,ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        preparedStatement.setString(8, User.getPresentUser().getUsername());
        preparedStatement.setInt(9, divisionID);

        //Run add customer
        try {
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            preparedStatement.close();
            return false;
        }

    }

    //Get Division ID
    public static Integer getDivisionID(String division) throws SQLException, Exception{
        int divisionID = -1;

        Statement statement = Database.connection().createStatement();

        String sqlStatement = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + division + "'";

        ResultSet result = statement.executeQuery(sqlStatement);

        while (result.next()) {
            divisionID = result.getInt("Division_ID");
        }
        return divisionID;

    }

}