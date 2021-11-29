package finalproject.application;
import finalproject.client.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Controller {
    // client data for current session
    private static Client client = new Client();
    private static String firstName;
    private static String lastName;
    private static String email;
    private static LocalDate dob;
    private static String address;
    private static String phone;
    private static ArrayList<String> accountIDs;

    // GUI components
    public TextField txtFirstName;
    public TextField txtLastName;
    public TextField txtEmail;
    public DatePicker dpDOB;
    public TextField txtAddress;
    public TextField txtPhone;
    public MenuButton menuAccountType;
    public TextField txtSSN;
    public PasswordField txtPassword;
    public Button btnCreateAccount;
    public Button btnLogin;

    public Controller() {
        // Attempts to connect the client to server on first initialization.
        if (!client.isConnected())
            client.connect();
    }

    // Sends a command to the server via the client, returns the server's response.
    private String sendCommand(String cmd) {
        String response;
        if (client.isConnected()) {
            try {
                response = client.sendRequest(cmd);
            } catch (IOException e) {
                response = "fail|" + e.getMessage();
            }
        }
        else {
            response = "fail|Client is not connected";
        }
        return response;
    }

    // Attempts to log into an account on the server with the provided credentials.
    public void login() throws IOException {
        // get the values inputted into the text fields
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // send login request to the server and receive server's response.
        String cmd = String.format("login|%s|%s", email, password);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        Alert alert;
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
//                // save user data from the server
//                this.firstName = respArgs[1];
//                this.lastName = respArgs[2];
//                this.email = respArgs[3];
//                this.dob = LocalDate.parse(respArgs[4]);
//                this.address = respArgs[5];
//                this.phone = respArgs[6];
//
//                // parse account IDs into an ArrayList
//                String accountIDsStr = respArgs[7];
//                String[] accountIDs =  accountIDsStr.replace("[","").replace("]","").split(",");
//                this.accountIDs = new ArrayList<>(Arrays.asList(accountIDs));

                // load the account selection scene
                Parent root = FXMLLoader.load(getClass().getResource("account_selection.fxml"));
                Stage stage = (Stage)this.btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root, 720, 480));

                // display success alert
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Login successful.", ButtonType.OK);
                alert.show();
                break;
            case "fail":
                // display failure alert with message from server
                alert = new Alert(Alert.AlertType.ERROR, respArgs[1], ButtonType.OK);
                alert.show();
                break;
        }
    }

    // Loads the create customer scene.
    public void loadCreateCustomer() throws IOException {
        // load the create account scene
        Parent root = FXMLLoader.load(getClass().getResource("create_customer.fxml"));
        Stage stage = (Stage)this.btnCreateAccount.getScene().getWindow();
        stage.setScene(new Scene(root, 720, 480));
    }

    // Attempts to create a new account on the server.
    public void createCustomer() {
        // get the values inputted into the text fields
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String ssn = txtSSN.getText();
        LocalDate dob = dpDOB.getValue();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();
        String password = txtPassword.getText();

        // send create customer request to the server and receive server's response.
        String cmd = String.format("customer|create|%s|%s|%s|%s|%s|%s|%s|%s",
                firstName, lastName, email, ssn, dob, address, phone, password);
        String response = sendCommand(cmd);

        // perform actions based on server's response

        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION, "Successfully created customer.", ButtonType.OK);
        alert.show();
    }
}
