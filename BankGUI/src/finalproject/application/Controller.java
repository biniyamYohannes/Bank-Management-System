package finalproject.application;
import finalproject.client.Client;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Controller {
    // client and account data for current session
    private static final Client client = new Client();
    // private static String ssn;
    private static String email;
    private static Account currentAccount;
    private static ArrayList<Account> accounts = new ArrayList<>();

    // GUI components
    private String selectedAccountType;
    public TextField txtFirstName;
    public TextField txtLastName;
    public TextField txtEmail;
    public DatePicker dpDOB;
    public TextField txtAddress;
    public TextField txtPhone;
    public MenuButton menuAccountType;
    public TextField txtSSN;
    public PasswordField txtPassword;
    public ListView<Account> listAccounts;
    public Button btnCreateAccount;
    public Button btnLogin;
    public Button btnAddAccount;
    public Button btnSelectAccount;

    public Controller() {
        // attempts to connect the client to server on first initialization
        if (!Controller.client.isConnected())
            Controller.client.connect();

        // initialize the view model for the list of accounts
        this.listAccounts = new ListView<>();
        this.listAccounts.setItems(FXCollections.observableArrayList(Controller.accounts));
    }

    // Gets the stage associated with a button click.
    private Stage getStage(ActionEvent actionEvent) {
        Node button = (Node) actionEvent.getSource();
        Stage stage = (Stage)button.getScene().getWindow();
        return stage;
    }

    // Loads a scene from an FXML file.
    private void loadScene(Stage stage, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stage.setScene(new Scene(root, 720, 480));
    }

    // Loads the create customer scene.
    public void loadCreateCustomer(ActionEvent actionEvent) throws IOException {
        Stage stage = getStage(actionEvent);
        this.loadScene(stage, "create_customer.fxml");
    }

    // Loads the account selection scene.
    public void loadAccountSelection(ActionEvent actionEvent) throws IOException {
        Stage stage = getStage(actionEvent);
        this.loadScene(stage, "account_selection.fxml");
    }

    // Handles the event that the user clicks on an account in the account list by setting it as the current account
    public void selectAccount(MouseEvent mouseEvent) {
        Controller.currentAccount = this.listAccounts.getSelectionModel().getSelectedItem();
    }

    // Loads the add account scene.
    public void loadAddAccount(ActionEvent actionEvent) throws IOException {
        Stage stage = getStage(actionEvent);
        this.loadScene(stage, "add_account.fxml");
    }

    // Loads the main account scene.
    public void loadAccountMain(ActionEvent actionEvent) throws IOException {
        Stage stage = getStage(actionEvent);

        // load the main account scene based on the current account's type
        String fxml = "";
        switch (Controller.currentAccount.getType()) {
            case "savings":
                fxml = "savings_main.fxml";
                break;
            case "checking":
                fxml = "checking_main.fxml";
                break;
            case "credit":
                fxml = "credit_main.fxml";
                break;
        }

        this.loadScene(stage, fxml);
    }

    // Sends a command to the server via the client, returns the server's response.
    private String sendCommand(String cmd) {
        String response;
        if (Controller.client.isConnected()) {
            try {
                response = Controller.client.sendRequest(cmd);
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
    public void login(ActionEvent actionEvent) throws IOException {
//        Request: login | email | password
//        Response: {success/fail} | fname | lname | email

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
//                // save the user's SSN from the server
//                Controller.ssn = respArgs[1];

                // save the user's email as the current email
                Controller.email = email;

                // load the user's accounts
                this.getAccounts();

                // load the account selection scene
                this.loadAccountSelection(actionEvent);

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

    // Fetches and returns the user's account IDs from the server.
    private String[] getAccountIDs() {
//        Request: customer | get | all
//        Response: {success/fail} | accountID1 | accountID2| accountID3 | ...

        String[] accountIDs;

        // send account IDs request to the server and receive server's response.
        String cmd = "customer|get|all";
        String response = sendCommand(cmd);

        // perform actions based on server's response
        Alert alert;
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // get the account IDs
                accountIDs = Arrays.copyOfRange(respArgs, 1, respArgs.length);
                break;
            case "fail":
                // return an empty array
                accountIDs = new String[]{};

                // display failure alert with message from server
                alert = new Alert(Alert.AlertType.ERROR, respArgs[1], ButtonType.OK);
                alert.show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + respArgs[0]);
        }

        return accountIDs;
    }

    // Request an account's information from the server, returns an Account object containing the account info.
    private Account getAccount(String accountID) {
        // Request: account | get | [accountId]
        // Response: {success/fail} | [Balance] | [AccountType] | {Saving: [interestRate] | Credit Card: [CreditLimit]}

        // send create customer request to the server and receive server's response.
        String cmd = String.format("account|get|%s", accountID);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        Alert alert;
        String[] respArgs = response.split("\\|");
        Account account = null;
        switch (respArgs[0]) {
            case "success":
                // get the server's responses
                float balance = Float.parseFloat(respArgs[1]);
                String accountType = respArgs[2];

                // create account depending on type
                if (Objects.equals(accountType, "savings"))
                    account = new SavingsAccount(accountID, accountType, balance, Float.parseFloat(respArgs[3]));
                else if (Objects.equals(accountType, "credit"))
                    account = new CreditAccount(accountID, accountType, balance, Float.parseFloat(respArgs[3]));
                else
                    account = new Account(accountID, accountType, balance);
                break;
            case "fail":
                // display failure alert with message from server
                alert = new Alert(Alert.AlertType.ERROR, respArgs[1], ButtonType.OK);
                alert.show();
                break;
        }

        return account;
    }

    // Fetches and stores the current user's accounts from the server.
    private void getAccounts() {
        // get the user's account IDs
        String[] accountIDs = this.getAccountIDs();

        // clear the user's list of accounts
        accounts.clear();

        // fetch and store information for each account
        for (String accountID : accountIDs) {
            Account account = this.getAccount(accountID);
            accounts.add(account);
        }

        // update the view model for the list of accounts
        this.listAccounts.setItems(FXCollections.observableArrayList(Controller.accounts));
    }
    
    // Attempts to create a new customer and account on the server.
    public void createCustomer(ActionEvent actionEvent) throws IOException {
//        Request: customer | create | [fname] | [lname] | [email] | [ssn] | [dob] | [address] | [phone] | password
//        Response: {success/fail} | errorMsg

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
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
//                // set the SSN as the current user's SSN
//                Controller.ssn = ssn;

                // set the email as the current user's email
                Controller.email = email;

                // send create account request to the server
                this.createAccount(actionEvent);
                break;
            case "fail":
                // display failure alert with message from server
                alert = new Alert(Alert.AlertType.ERROR, respArgs[1], ButtonType.OK);
                alert.show();
                break;
        }
    }

    // Sets the user-selected account type as "savings".
    public void setSavingsType(){
        this.menuAccountType.setText("Savings");
        this.selectedAccountType = "savings";
    };

    // Sets the user-selected account type as "checking".
    public void setCheckingType(){
        this.menuAccountType.setText("Checking");
        this.selectedAccountType = "checking";
    };

    // Sets the user-selected account type as "credit".
    public void setCreditType(){
        this.menuAccountType.setText("Credit");
        this.selectedAccountType = "credit";
    };

    // Attempts to create a new account on the server.
    public void createAccount(ActionEvent actionEvent) throws IOException {
//        Request: account | create | [email] | [accountType]
//        Response: {success/fail} | {accountID/errorMsg}

        // send create customer request to the server and receive server's response
        String cmd = String.format("account|create|%s|%s", Controller.email, this.selectedAccountType);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        Alert alert;
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // get the new account's ID
                String accountID = respArgs[1];

                // get the new account's information
                Account newAccount = this.getAccount(accountID);

                // set the new account as the current account
                Controller.currentAccount = newAccount;

                // add the new account to the user's list of accounts
                Controller.accounts.add(newAccount);

                // update the view model for the list of accounts
                this.listAccounts.setItems(FXCollections.observableArrayList(Controller.accounts));

                // load the main account scene based on account type
                this.loadAccountMain(actionEvent);

                // display success alert
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Account successfully created.", ButtonType.OK);
                alert.show();
                break;
            case "fail":
                // display failure alert with message from server
                alert = new Alert(Alert.AlertType.ERROR, respArgs[1], ButtonType.OK);
                alert.show();
                break;
        }
    }
}
