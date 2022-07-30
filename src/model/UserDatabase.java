package model;

import static misc.Database.getConnection;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import misc.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class UserDatabase {

    private static User presentUser;

    public static User getPresentUser() {
        return presentUser;
    }

    //Login Attempt
    public static Boolean login(String username, String password) {
        try {
            Database.getConnection();
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM users WHERE User_Name=? AND Password=?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: ");
            return false;
        }
    }
}