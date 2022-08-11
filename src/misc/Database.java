/**
 * Database connection
 */

package misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String DatabaseName = "client_schedule";
    private static final String DatabaseURL = "jdbc:mysql://localhost:3306/" + DatabaseName;
    private static final String DatabaseUsername = "sqlUser";
    private static final String DatabasePassword = "Passw0rd!";
    private static final String DatabaseJDBCDriver = "com.mysql.jdbc.Driver";
    public static Connection connection;

    public Database() {};

    //Open Connection
    public static void openConnection() throws ClassNotFoundException, SQLException {

        Class.forName(DatabaseJDBCDriver);
        connection = (Connection) DriverManager.getConnection(DatabaseURL, DatabaseUsername, DatabasePassword);
        System.out.println("Connection Open");
    }

    //Return Connection
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DatabaseURL, DatabaseUsername, DatabasePassword);
        return connection;
    }

    //Close Connection
    public static void closeConnection() throws ClassNotFoundException, SQLException {
        connection.close();
        System.out.println("Connection Closed");
    }

    public static Connection connection () {return connection;};

}
