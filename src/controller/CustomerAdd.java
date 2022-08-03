package controller;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Customer;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerAdd implements Initializable {

    Stage stage;
    Parent scene;

    @FXML private TextField CustomerNameText;
    @FXML private TextField AddressText;
    @FXML private TextField PostalCodeText;
    @FXML private TextField PhoneText;
    @FXML ComboBox<String> DivisionComboBox;

    //Save Customer Button
    @FXML
    public void onActionSave(ActionEvent event) throws Exception {
            //Collect information user has entered
            String name = CustomerNameText.getText();
            String address = AddressText.getText();
            String postalCode = PostalCodeText.getText();
            String phone = PhoneText.getText();
            String division = DivisionComboBox.getValue();

            //Empty field alert to user
            if (name.isBlank() || address.isBlank() || postalCode.isBlank() || phone.isBlank() || division.isBlank()) {
                ButtonType clickOK = new ButtonType("Understand", ButtonBar.ButtonData.OK_DONE);
                Alert emptyField = new Alert(Alert.AlertType.ERROR, "Please make sure all fields are filled in.", clickOK);
                emptyField.showAndWait();
                return;
            }

            //Add Customer to Database
            Boolean success = Customer.addCustomer(name, address, postalCode, phone, Customer.getDivisionID(division));

            //Success/Failure Alerts
            if (success) {
                ButtonType clickOK = new ButtonType("Successful/Main Screen", ButtonBar.ButtonData.OK_DONE);
                Alert emptyField = new Alert(Alert.AlertType.CONFIRMATION, "Customer has been successfully added to the database.", clickOK);
                emptyField.showAndWait();
                //Return to Main Screen after successful customer Add
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            } else {
                ButtonType clickOK = new ButtonType("Understand", ButtonBar.ButtonData.OK_DONE);
                Alert emptyField = new Alert(Alert.AlertType.CONFIRMATION, "Customer has been not been added to the database - check fields.", clickOK);
                emptyField.showAndWait();
                return;
            }
    }

        //Reset Customer Fields
        @FXML
        void onActionReset (ActionEvent event){
            CustomerNameText.clear();
            AddressText.clear();
            PostalCodeText.clear();
            PhoneText.clear();
            DivisionComboBox.getItems().clear();

        }

        //Cancel & Return to Main Screen
        @FXML
        void onActionMain (ActionEvent event) throws IOException {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }


        @Override
        public void initialize(URL url, ResourceBundle resourceBundle){
            //Populate Division Combo box
            try {
                DivisionComboBox.setItems(Customer.getAllDivisions());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
