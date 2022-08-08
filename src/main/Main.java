/**
 * Schedule Application
 * Author: Beth Fuller
 * Summer 2022
 */
package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import misc.Database;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {
      @Override
    public void start(Stage stage) throws Exception {
         Parent root = FXMLLoader.load(getClass().getResource("/view/LoginScreen.fxml"));
         stage.setTitle("Schedule");
         stage.setScene(new Scene(root, 600, 400));
         stage.show();
     }

    public static void main (String[] args) throws SQLException, ClassNotFoundException {
          Database.openConnection();
          launch(args);
    }
 }


