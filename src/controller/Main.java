package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import misc.Database;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements Initializable {

    Stage stage;
    Parent scene;

    //Calendar things
    @FXML private DatePicker datePicker;
    @FXML private ToggleGroup CalendarToggle;
    @FXML private RadioButton AppointmentViewAllRB;
    @FXML private RadioButton AppointmentViewMonthRB;
    @FXML private RadioButton AppointmentViewWeekRB;
    private boolean calendarWeekly;
    private boolean calendarMonthly;

    //Appointment Table
    @FXML TableView<Appointment> appointmentTableView;
    @FXML TextField AppointmentSearchText;
    @FXML TableColumn<Appointment, Integer> AppointmentIDCol;
    @FXML TableColumn<Appointment, String> AppointmentTitleCol;
    @FXML TableColumn<Appointment, String> AppointmentDescriptionCol;
    @FXML TableColumn<Appointment, String> AppointmentLocationCol;
    @FXML TableColumn<Appointment, String> AppointmentTypeCol;
    @FXML TableColumn<Appointment, ZonedDateTime> AppointmentStartCol;
    @FXML TableColumn<Appointment, ZonedDateTime> AppointmentEndCol;
    @FXML TableColumn<Appointment, Integer> AppointmentCustomerIDCol;
    @FXML TableColumn<Appointment, Integer> AppointmentUserIDCol;
    @FXML TableColumn<Appointment, Integer> AppointmentContactIDCol;

    //Customer Table
    @FXML TableView<Customer> customerTableView;
    @FXML TextField CustomerSearchText;
    @FXML TableColumn<Customer, Integer> CustomerIDCol;
    @FXML TableColumn<Customer, String> CustomerNameCol;
    @FXML TableColumn<Customer, String> CustomerAddressCol;
    @FXML TableColumn<Customer, String> CustomerPostalCodeCol;
    @FXML TableColumn<Customer, String> CustomerDivisionCol;
    @FXML TableColumn<Customer, String> CustomerPhoneCol;
    @FXML TableColumn<Customer, Integer> CustomerDivisionIDCol;

    private static Customer CustomerToModify;
    private static Appointment AppointmentToModify;

    //Toggle Group - All, Week, Month
    public void startCalendarToggle() {
        CalendarToggle = new ToggleGroup();
        AppointmentViewAllRB.setToggleGroup(CalendarToggle);
        AppointmentViewMonthRB.setToggleGroup(CalendarToggle);
        AppointmentViewWeekRB.setToggleGroup(CalendarToggle);
    }

    //Get Customer/Appointment selected object
    public static Customer getCustomerToModify(){return CustomerToModify;}
    public static Appointment getAppointmentToModify(){return AppointmentToModify;}

    //Convert UTC database time to user's local time
    public static LocalDateTime convert (LocalDate Date, String Time) {
        String string = Date + "" + Time + "00:0";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime dateTime = LocalDateTime.parse(string, formatter);
        System.out.println(dateTime);
        return  dateTime;
    }

    //Observable Lists - Appointment & Customer
    ObservableList<Customer> customerTable = FXCollections.observableArrayList();
    ObservableList<Appointment> appointmentTable = FXCollections.observableArrayList();

    @FXML void onActionSearchAppointment(ActionEvent event) {

    }

    //View All Calendar
    public void onActionViewAll(ActionEvent event) {
        AppointmentViewMonthRB.setSelected(false);
        AppointmentViewMonthRB.setSelected(false);





    }

    //View Month Calendar
    public void onActionViewMonth() {}

    //View Week Calendar
    public void onActionViewWeek() {}


    //Open Add Appointment Screen
    @FXML
    void onActionOpenAddAppointment(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AppointmentAddScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    //Open Modify Appointment Screen
    @FXML
    void onActionOpenModifyAppointment(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AppointmentModifyScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionDeleteAppointment(ActionEvent event) {

    }

    //Customer Name Search
    public Customer lookupCustomer (String searchName) {
        for (Customer customerSearch : customerTable) {
            if (customerSearch.getName().equalsIgnoreCase(searchName)) {
                return customerSearch;
            }
        }
        return null;
}

    //Fill Appointments
    public void fillAppointments(ObservableList<Appointment> fillList) {
        AppointmentIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("id"));
        AppointmentTitleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        AppointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        AppointmentLocationCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        AppointmentStartCol.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("start"));
        AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<Appointment, ZonedDateTime>("end"));
        AppointmentCustomerIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("customerID"));
        AppointmentUserIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("userID"));
        AppointmentContactIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("contactID"));

        appointmentTableView.setItems(fillList);
    }

    //Search Customer Name Button
    @FXML
    void onActionSearchCustomers(ActionEvent event) {
        String searchCustomerString = CustomerSearchText.getText();
        if (!searchCustomerString.isEmpty()) {
            customerTableView.getSelectionModel().select(lookupCustomer(searchCustomerString));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Product not found.");
            alert.showAndWait();
        }
    }

    //Open Add Customer Screen
    @FXML
    void onActionOpenAddCustomer(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/CustomerAddScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    //Open Modify Customer Screen
    @FXML
    void onActionOpenModifyCustomer(ActionEvent event) throws IOException, SQLException {
        CustomerToModify = customerTableView.getSelectionModel().getSelectedItem();

        //No Customer Selected
        if (CustomerToModify == null) {
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.WARNING, "No customer selected.", ok);
            alert.showAndWait();
            return;
        } else {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/CustomerModifyScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
}

    //Delete Customer Button
    @FXML
    void onActionDeleteCustomer(ActionEvent event) throws SQLException, IOException {
        Customer selectCustomer = customerTableView.getSelectionModel().getSelectedItem();

            if (selectCustomer == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setContentText("No customer is selected.");
                alert.showAndWait();
            }
            else {

                ButtonType yes = ButtonType.YES;
                ButtonType no = ButtonType.NO;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete the selected customer?", yes, no);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    Boolean deleteCustomerSuccess = Customer.deleteCustomer(selectCustomer.getId());
                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();

                    if (deleteCustomerSuccess) {
                        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        Alert deletedCustomer = new Alert(Alert.AlertType.CONFIRMATION, "Customer Deleted", ok);
                        deletedCustomer.showAndWait();
                    }
                    else {
                        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        Alert notDeletedCustomer = new Alert(Alert.AlertType.CONFIRMATION, "Customer has not been deleted. Customer" +
                                "cannot be deleted unless all customer appointments are deleted.", ok);
                        notDeletedCustomer.showAndWait();
                    }
                }

                else return;
            }

        }

    //Open Reports Screen
    @FXML
    void onActionOpenReports(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/ReportScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    //Logoff/Exit Application
    @FXML
    void onActionLogoff(ActionEvent event) {System.exit(0);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        //Populate initial table view to View All
        AppointmentViewAllRB.setSelected(true);
        startCalendarToggle();

        ObservableList<Appointment> allAppointments = null;
        try {
            allAppointments = Appointment.getAllAppointments();
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        fillAppointments(allAppointments);

        //Customer Database Table
        Connection connect;
        try {
            connect = Database.getConnection();
            ResultSet resultSet = connect.createStatement().executeQuery(
                    "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Division, Phone, customers.Division_ID\n" +
                        "FROM client_schedule.customers\n" +
                        "INNER JOIN client_schedule.first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID;");
            while (resultSet.next()) {
                customerTable.add(new Customer(resultSet.getString("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Division"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Division_ID")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomerIDCol.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        CustomerNameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        CustomerAddressCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        CustomerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
        CustomerDivisionCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("division"));
        CustomerPhoneCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
        CustomerDivisionIDCol.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("divisionID"));

        customerTableView.setItems(customerTable);
    }
}
