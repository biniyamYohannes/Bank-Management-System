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
import java.util.Arrays;

public class Controller {
    // client data for current session
    private final Client client;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
    private String address;
    private String phone;
    private ArrayList<String> accountIDs;

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
        client = new Client();
        client.connect();
    }

    // Sends a command to the server via the client, returns the server's response.
    private String sendCommand(String cmd) {
        String response;
        if (client.isConnected()) {
            try {
                response = client.sendRequest(cmd);
            } catch (IOException e) {
                response = "ERR|" + e.getMessage();
            }
        }
        else {
            response = "ERR|Client is not connected";
        }
        return response;
    }

    // Attempts to log into an account on the server with the provided credentials.
    public void login() throws IOException {
        // get the values inputted into the text fields
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // send login request to the server and receive server's response.
        Alert alert;
        String cmd = String.format("login|%s|%s", email, password);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // save user data from the server
                this.firstName = respArgs[1];
                this.lastName = respArgs[2];
                this.email = respArgs[3];
                this.dob = LocalDate.parse(respArgs[4]);
                this.address = respArgs[5];
                this.phone = respArgs[6];

                // parse account IDs into an ArrayList
                String accountIDsStr = respArgs[7];
                String[] accountIDs =  accountIDsStr.replace("[","").replace("]","").split(",");
                this.accountIDs = new ArrayList<>(Arrays.asList(accountIDs));

                // load the menu scene
                Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
                Stage stage = (Stage)this.btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root, 720, 480));

                // display success alert
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Login successful.", ButtonType.OK);
                alert.show();
                break;
            case "fail":
                alert = new Alert(Alert.AlertType.ERROR, respArgs[1], ButtonType.OK);
                alert.show();
                break;
        }
    }

    // Loads the create account scene.
    public void loadCreateAccount() throws IOException {
        // load the create account scene
        Parent root = FXMLLoader.load(getClass().getResource("create_account.fxml"));
        Stage stage = (Stage)this.btnLogin.getScene().getWindow();
        stage.setScene(new Scene(root, 720, 480));
    }

    // Attempts to create a new account on the server.
    public void createAccount() {

    }
}
