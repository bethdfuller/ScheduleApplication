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
import misc.ConvertTimeZoneInterface;
import misc.Database;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements Initializable {

    Stage stage;
    Parent scene;

    //Calendar things
    @FXML private DatePicker pickDate;
    @FXML private ToggleGroup CalendarToggle;
    @FXML private RadioButton AppointmentViewAllRB;
    @FXML private RadioButton AppointmentViewMonthRB;
    @FXML private RadioButton AppointmentViewWeekRB;
    private boolean calendarWeekly;
    private boolean calendarMonthly;

    //Appointment Table
    @FXML TableView<Appointment> appointmentTableView;
    @FXML TextField AppointmentSearchText;
    @FXML
    TableColumn<Appointment, String> AppointmentIDCol;
    @FXML TableColumn<Appointment, String> AppointmentTitleCol;
    @FXML TableColumn<Appointment, String> AppointmentDescriptionCol;
    @FXML TableColumn<Appointment, String> AppointmentLocationCol;
    @FXML TableColumn<Appointment, String> AppointmentTypeCol;
    @FXML
    TableColumn<Appointment, String> AppointmentStartCol;
    @FXML
    TableColumn<Appointment, String> AppointmentEndCol;
    @FXML
    TableColumn<Appointment, String> AppointmentCustomerIDCol;
    @FXML
    TableColumn<Appointment, String> AppointmentUserIDCol;
    @FXML
    TableColumn<Appointment, String> AppointmentContactIDCol;

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

    ZonedDateTime startDateRange;
    ZonedDateTime endDateRange;

    ConvertTimeZoneInterface conversion = (String dateTime) -> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt =  LocalDateTime.parse(dateTime, format).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    };

    @FXML private void AppointmentViewWeekRB (ActionEvent event) throws IOException, SQLException, ClassNotFoundException {onActionViewWeek();}
    @FXML private void AppointmentViewMonthRB (ActionEvent event) throws IOException, SQLException, ClassNotFoundException {onActionViewMonth();}
    @FXML private void AppointmentViewAllRB (ActionEvent event) throws IOException, SQLException, ClassNotFoundException {onActionViewAll();}

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

    //Observable Lists - Appointment & Customer
    ObservableList<Customer> customerTable = FXCollections.observableArrayList();
    ObservableList<Appointment> appointmentTable = FXCollections.observableArrayList();

    @FXML void onActionSearchAppointment(ActionEvent event) {

    }

    //Date Picker Action
    @FXML private void onActionPickDate(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        if (calendarWeekly) {
            onActionViewWeek();
        }
        else if (calendarMonthly) {
            onActionViewMonth();
        }
        else {
            onActionViewAll();
        }
    }

    //View All Calendar
    public void onActionViewAll() {
        calendarWeekly = false;
        calendarMonthly = false;

        Connection connect;
        try {
            appointmentTable.clear();
            connect = Database.getConnection();
            ResultSet allAppointmentResults = connect.createStatement().executeQuery("" +
                    "SELECT Appointment_ID, Title, Description, Location, Type, Start, End,\n" +
                    "customers.Customer_ID, users.User_ID, contacts.Contact_ID\n" +
                    "FROM client_schedule.appointments\n" +
                    "INNER JOIN client_schedule.customers ON appointments.Customer_ID = customers.Customer_ID\n" +
                    "INNER JOIN client_schedule.users ON appointments.User_ID = users.User_ID\n" +
                    "INNER JOIN client_schedule.contacts ON appointments.Contact_ID = contacts.Contact_ID");
            while (allAppointmentResults.next()) {
                LocalDateTime timeZoneStart = conversion.stringLocalDateTime(allAppointmentResults.getString("Start"));
                LocalDateTime timeZoneEnd = conversion.stringLocalDateTime(allAppointmentResults.getString("End"));
                String timeZoneStartString = timeZoneStart.toString().substring(0, 16);
                String timeZoneEndString = timeZoneEnd.toString().substring(0,16);
                
                appointmentTable.add(new Appointment(allAppointmentResults.getString("Appointment_ID"),
                    allAppointmentResults.getString("Title"),
                    allAppointmentResults.getString("Description"),
                    allAppointmentResults.getString("Location"),
                    allAppointmentResults.getString("Type"),
                    timeZoneStartString,
                    timeZoneEndString,
                    allAppointmentResults.getString("Customer_ID"),
                    allAppointmentResults.getString("User_ID"),
                    allAppointmentResults.getString("Contact_ID")));
            }
            appointmentTableView.setItems(appointmentTable);
        } catch (SQLException e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            }
    }


    //View Month Calendar
    public void onActionViewMonth() throws SQLException, ClassNotFoundException {
        calendarWeekly = false;
        calendarMonthly = false;

        LocalDate localDate = pickDate.getValue();
        String pickMonthString = pickDate.toString().substring(5,7);
        String pickYearString = pickDate.getValue().toString().substring(0,4);

        System.out.println("Month: " + pickMonthString + "Year: " + pickYearString);

        Connection connect;
        try {
            appointmentTable.clear();
            connect = Database.getConnection();
            ResultSet weekAppointmentResults = connect.createStatement().executeQuery(String.format(
                    "SELECT Appointment_ID, Title, Description, Location, Type, Start, End,\n" +
                            "customers.Customer_ID, users.User_ID, contacts.Contact_ID\n" +
                            "FROM client_schedule.appointments\n" +
                            "INNER JOIN client_schedule.customers ON appointments.Customer_ID = customers.Customer_ID\n" +
                            "INNER JOIN client_schedule.users ON appointments.User_ID = users.User_ID\n" +
                            "INNER JOIN client_schedule.contacts ON appointments.Contact_ID = contacts.Contact_ID\n" +
                            "WHERE MONTH(Start) = '%s' AND YEAR(Start) = '%s'\n" +
                            "ORDER BY Start", pickMonthString, pickYearString));
            while (weekAppointmentResults.next()) {
                LocalDateTime timeZoneStart = conversion.stringLocalDateTime(weekAppointmentResults.getString("Start"));
                LocalDateTime timeZoneEnd = conversion.stringLocalDateTime(weekAppointmentResults.getString("End"));
                String timeZoneStartString = timeZoneStart.toString().substring(0, 16);
                String timeZoneEndString = timeZoneEnd.toString().substring(0,16);

                appointmentTable.add(new Appointment(weekAppointmentResults.getString("Appointment_ID"),
                        weekAppointmentResults.getString("Title"),
                        weekAppointmentResults.getString("Description"),
                        weekAppointmentResults.getString("Location"),
                        weekAppointmentResults.getString("Type"),
                        timeZoneStartString,
                        timeZoneEndString,
                        weekAppointmentResults.getString("Customer_ID"),
                        weekAppointmentResults.getString("User_ID"),
                        weekAppointmentResults.getString("Contact_ID")));
            }
            appointmentTableView.setItems(appointmentTable);
            if(appointmentTable.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setContentText("No appointments during selected month.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //View Week Calendar
    public void onActionViewWeek() throws SQLException, ClassNotFoundException {
        calendarWeekly = true;
        calendarMonthly = false;

        LocalDate localDate = pickDate.getValue();
        String pickYearString = pickDate.getValue().toString().substring(0,4);
        WeekFields weekFields = WeekFields.of(Locale.US);
        int pickWeek = localDate.get(weekFields.weekOfWeekBasedYear());
        String pickWeekString = Integer.toString(pickWeek);

        System.out.println("Week: " + pickWeekString + "Year: " + pickYearString);

        Connection connect;
        try {
            appointmentTable.clear();
            connect = Database.getConnection();
            ResultSet weekAppointmentResults = connect.createStatement().executeQuery(String.format(
                    "SELECT Appointment_ID, Title, Description, Location, Type, Start, End,\n" +
                    "customers.Customer_ID, users.User_ID, contacts.Contact_ID\n" +
                    "FROM client_schedule.appointments\n" +
                    "INNER JOIN client_schedule.customers ON appointments.Customer_ID = customers.Customer_ID\n" +
                    "INNER JOIN client_schedule.users ON appointments.User_ID = users.User_ID\n" +
                    "INNER JOIN client_schedule.contacts ON appointments.Contact_ID = contacts.Contact_ID\n" +
                    "WHERE WEEK(DATE(Start))+1 = '%s' AND YEAR(Start) = '%s'\n" +
                    "ORDER BY Start", pickWeek, pickYearString));
            while (weekAppointmentResults.next()) {
                LocalDateTime timeZoneStart = conversion.stringLocalDateTime(weekAppointmentResults.getString("Start"));
                LocalDateTime timeZoneEnd = conversion.stringLocalDateTime(weekAppointmentResults.getString("End"));
                String timeZoneStartString = timeZoneStart.toString().substring(0, 16);
                String timeZoneEndString = timeZoneEnd.toString().substring(0,16);

                appointmentTable.add(new Appointment(weekAppointmentResults.getString("Appointment_ID"),
                        weekAppointmentResults.getString("Title"),
                        weekAppointmentResults.getString("Description"),
                        weekAppointmentResults.getString("Location"),
                        weekAppointmentResults.getString("Type"),
                        timeZoneStartString,
                        timeZoneEndString,
                        weekAppointmentResults.getString("Customer_ID"),
                        weekAppointmentResults.getString("User_ID"),
                        weekAppointmentResults.getString("Contact_ID")));
            }
            appointmentTableView.setItems(appointmentTable);
            if(appointmentTable.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setContentText("No appointments during selected week.");
                    alert.showAndWait();
                }
        } catch (SQLException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }


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
        AppointmentIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("id"));
        AppointmentTitleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        AppointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        AppointmentLocationCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        AppointmentStartCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("start"));
        AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("end"));
        AppointmentCustomerIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerID"));
        AppointmentUserIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("userID"));
        AppointmentContactIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactID"));

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

        AppointmentIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("id"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        AppointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        AppointmentLocationCol.setCellValueFactory(new PropertyValueFactory<Appointment,String>("location"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment,String>("type"));
        AppointmentStartCol.setCellValueFactory(new PropertyValueFactory<Appointment,String>("start"));
        AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("end"));
        AppointmentCustomerIDCol.setCellValueFactory(new PropertyValueFactory<Appointment,String>("customerID"));
        AppointmentUserIDCol.setCellValueFactory(new PropertyValueFactory<Appointment,String>("userID"));
        AppointmentContactIDCol.setCellValueFactory(new PropertyValueFactory<Appointment,String>("contactID"));


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
