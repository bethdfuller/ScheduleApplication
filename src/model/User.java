/**
 * User Model
 */

package model;

import misc.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;

import static misc.Database.connection;
import static misc.Database.getConnection;

public class User {

    private String username;
    private Integer userID;

    private static User presentUser;
    private static Locale presentUserLocale;
    private static ZoneId presentUserTimeZone;

    //Get/Set
    public static User getPresentUser() {return presentUser;}
    public static Locale getPresentUserLocale(){return presentUserLocale;}


    //Public User
    public User(String enterUsername, Integer enterUserID) {
        username = enterUsername;
        userID = enterUserID;
    }

    //Get/Set
    public String getUsername() {return username;}
    public Integer getUserID() {return userID;}


    //Login Attempt
    public static Boolean login(String username, String password) {
        try {
            Database.getConnection();
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM users WHERE User_Name=? AND Password=?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return false;
            } else {
                presentUser = new User(resultSet.getString("User_Name"), resultSet.getInt("User_ID"));
                presentUserLocale = Locale.getDefault();
                presentUserTimeZone = ZoneId.systemDefault();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error: ");
            return false;
        }
    }
}