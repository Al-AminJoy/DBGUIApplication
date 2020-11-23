package db.gui.application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Al-Amin Islam <alaminislam3555@gmail.com>
 */
public class FXMLDocumentController implements Initializable {

    private Connection connection = null;
    @FXML
    private TextField sinField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField numberField;
    @FXML
    private DatePicker hiringField;
    @FXML
    private Button addButton;
    @FXML
    private Button resetButton;

    @FXML
    private Label statusField;
    @FXML
    private ListView<Employee> employeeListView;
    private ObservableList<Employee> employeeList;
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    private TableColumn<Employee, String> sinColumn;
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> phoneColumn;
    @FXML
    private TableColumn<?, ?> hiringDateColumn;
   // private TableColumn<Employee, LocalDate> hiringDateColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        readData();

    }

    private Connection getConnection() {
        final String USERNAME = "root";
        final String PASSWORD = "";
        final String DBNAME = "rentaldb";
        final String HOSTNAME = "localhost";
        final String URL = "jdbc:mysql://" + HOSTNAME + "/" + DBNAME;

        Connection connection = null;

        try {
            if (connection == null) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }

        } catch (SQLException ex) {
            System.err.println("Failed");
            statusField.setText("Failed to Add");
            System.err.println(ex);
        }
        return connection;
    }

    @FXML
    private void addClientAction(ActionEvent event) {

        try {
            connection = getConnection();
            String sin = sinField.getText();
            String name = nameField.getText();
            String number = numberField.getText();
            LocalDate hiringDate = hiringField.getValue();
            String query = String.format("insert into employee values ('%s','%s','%s','%s')", sin, name, number, hiringDate.toString());
            Statement statement;
            statement = connection.createStatement();
            statement.executeUpdate(query);
            Employee employee=new Employee(sin,name,number,hiringDate);
            employeeList.add(employee);
            statusField.setText("Successfully Added");
            System.out.println("Connected");
            clearForm();
        } catch (SQLException ex) {
            System.err.println("Failed");
            statusField.setText("Failed to Add");
            System.err.println(ex);
        }

    }

    @FXML
    private void resetClientAction(ActionEvent event) {
        clearForm();
        statusField.setText(null);
        sinField.setDisable(false);
    }

    private void clearForm() {
        sinField.clear();
        nameField.clear();
        numberField.clear();
        hiringField.setValue(null);
    }

    private void readData() {
        employeeList = FXCollections.observableArrayList();
       
        try {
            connection = getConnection();
            String query = "select * from employee";
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String sin = resultset.getString("sin");
                String name = resultset.getString("name");
                String phone = resultset.getString("phone");
                LocalDate hiringDate = LocalDate.parse(resultset.getString("hiringDate"));
                Employee employee = new Employee(sin, name, phone, hiringDate);
                employeeList.add(employee);
                employeeListView.setItems(employeeList);
                employeeTableView.setItems(employeeList);
                sinColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSin()) );
                nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()) );
                phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()) );
               // hiringDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHiringDate()) );

            }
        } catch (SQLException ex) {
            System.err.println("Failed");
            System.err.println(ex);
        }
    }

    @FXML
    private void handleMouseClickedAction(MouseEvent event) {
        Employee employee = employeeListView.getSelectionModel().getSelectedItem();
        if (employee != null) {
            sinField.setDisable(true);
            showEmployee(employee);
        }
        

    }

    private void showEmployee(Employee employee) {
        sinField.setText(employee.getSin());
        nameField.setText(employee.getName());
        numberField.setText(employee.getPhone());
        hiringField.setValue(employee.getHiringDate());

    }

    @FXML
    private void updateClientAction(ActionEvent event) {
        try {
            connection = getConnection();
            String sin = sinField.getText();
            String name = nameField.getText();
            String number = numberField.getText();
            LocalDate hiringDate = hiringField.getValue();
            String query = String.format("UPDATE employee SET name='%s',phone='%s',hiringDate='%s' WHERE sin='%s' " ,name, number, hiringDate.toString(),sin);
            Statement statement;
            statement = connection.createStatement();
            statement.executeUpdate(query);
             Employee employee = new Employee(sin, name, number, hiringDate);
            employeeList.set(employeeListView.getSelectionModel().getSelectedIndex(), employee);
            statusField.setText("Successfully Updated");
            clearForm();
        } catch (SQLException ex) {
            System.err.println("Failed");
            statusField.setText("Failed to Update");
            System.err.println(ex);
        }
    }

    @FXML
    private void deleteClientAction(ActionEvent event) {
        try {
            connection = getConnection();
            String sin = sinField.getText();
           
            String query = String.format("DELETE FROM employee WHERE sin='%s' " ,sin);
            Statement statement;
            statement = connection.createStatement();
            statement.executeUpdate(query);      
            employeeList.remove(employeeListView.getSelectionModel().getSelectedIndex());
            statusField.setText("Successfully Deleted");
            clearForm();
        } catch (SQLException ex) {
            System.err.println("Failed");
            statusField.setText("Failed to Delete");
            System.err.println(ex);
        }
    }

}
