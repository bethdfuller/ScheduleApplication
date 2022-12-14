

package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import misc.Database;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Customer Modify Appointment Controller. The lambda expression takes Country input from the user to create a Division Combo Box that aligns with the selected Country.
 * It is located in initialize of the Customer Add method @override where the stage is being set.
 */

public class CustomerModify implements Initializable {

    Stage stage;
    Parent scene;

    @FXML Label CustomerIDLabel;
    @FXML TextField CustomerNameText;
    @FXML TextField AddressText;
    @FXML TextField PostalCodeText;
    @FXML TextField PhoneText;
    @FXML ComboBox<String> DivisionComboBox;
    @FXML ComboBox<String> CountryComboBox;

    private Customer CustomerToModify;

    //Save Modified Customer
    @FXML
    public void onActionSave(ActionEvent event) throws Exception {
        Integer id = Integer.parseInt(CustomerIDLabel.getText());
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
        Boolean success = Customer.updateCustomer(name, address, postalCode, phone, Customer.getDivisionID(division), id);

        //Success/Failure Alerts
        if (success) {
            ButtonType clickOK = new ButtonType("Successful/Main Screen", ButtonBar.ButtonData.OK_DONE);
            Alert emptyField = new Alert(Alert.AlertType.CONFIRMATION, "Customer has been successfully updated.", clickOK);
            emptyField.showAndWait();
            //Return to Main Screen after successful customer Add
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        } else {
            ButtonType clickOK = new ButtonType("Understand", ButtonBar.ButtonData.OK_DONE);
            Alert emptyField = new Alert(Alert.AlertType.CONFIRMATION, "Customer has been not been updated.", clickOK);
            emptyField.showAndWait();
            return;
        }

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    //Cancel/Return to Main Screen
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Initazlie the stage & items. The lambda expression takes Country input from the user to create a Division Combo Box that aligns with the selected Country.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    //Load Customer to Modify from selection
    CustomerToModify = Main.getCustomerToModify();

    CustomerIDLabel.setText(String.valueOf(CustomerToModify.getId()));
    CustomerNameText.setText(CustomerToModify.getName());
    AddressText.setText(CustomerToModify.getAddress());
    PostalCodeText.setText(CustomerToModify.getPostalCode());
    PhoneText.setText(CustomerToModify.getPhone());
    DivisionComboBox.getSelectionModel().select(CustomerToModify.getDivision());
    CountryComboBox.getSelectionModel().select(CustomerToModify.getCountry());

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