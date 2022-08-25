

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

/**
 * Customer Add controller. The lambda expression takes Country input from the user to create a Division Combo Box that aligns with the selected Country.
 * It is located in initialize of the Customer Add method @override where the stage is being set.
 */

public class CustomerAdd implements Initializable {

    Stage stage;
    Parent scene;

    @FXML private TextField CustomerNameText;
    @FXML private TextField AddressText;
    @FXML private TextField PostalCodeText;
    @FXML private TextField PhoneText;
    @FXML ComboBox<String> DivisionComboBox;
    @FXML ComboBox<String> CountryComboBox;

    //Save Customer Button
    @FXML
    public void onActionSave(ActionEvent event) throws Exception {
        //Collect information user has entered
        String name = CustomerNameText.getText();
        String address = AddressText.getText();
        String postalCode = PostalCodeText.getText();
        String phone = PhoneText.getText();
        String division = DivisionComboBox.getValue();
        String country = CountryComboBox.getValue();

        //Empty field alert to user
        if (name.isBlank() || address.isBlank() || postalCode.isBlank() || phone.isBlank() || division.isBlank() || country.isBlank()) {
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
        }
    }

    //Reset Customer Fields
    @FXML
    void onActionReset(ActionEvent event) throws IOException {
        CustomerNameText.clear();
        AddressText.clear();
        PostalCodeText.clear();
        PhoneText.clear();
        DivisionComboBox.getItems().clear();
        CountryComboBox.getItems().clear();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/CustomerAddScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();

    }

    //Cancel & Return to Main Screen
    @FXML
    void onActionMain(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Lambda & Listener used for Division Combo/Country Combo
        try {
            CountryComboBox.setItems(Customer.getAllCountries());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //Lambda that takes Country input from the user to create a Division Combo Box that aligns with the selected Country
        CountryComboBox.valueProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null) {
                DivisionComboBox.getItems().clear();
                DivisionComboBox.setDisable(true);
            } else {
                DivisionComboBox.setDisable(false);
                try {
                    DivisionComboBox.setItems(Customer.getRelevantDivision(CountryComboBox.getValue()));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}