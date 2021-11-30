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
import java.util.Objects;

public class Controller {
    // client and account data for current session
    private static Client client = new Client();
    private static String ssn;
    private static String currentAccountID;
    private static ArrayList<Account> accounts;

    // GUI components
    private String selectedAccountType; // user selected account type during account creation
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
                // save the user's SSN from the server.
                ssn = respArgs[1];

                // load the user's accounts
                this.getAccounts();

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

    // Fetches and returns the user's account IDs from the server.
    public String[] getAccountIDs() {
//        Request: customer | get | accountIDs
//        Response: {success/fail} | accountID1 | accountID2| accountID3 | ...

        String[] accountIDs;

        // send account IDs request to the server and receive server's response.
        String cmd = "customer|get|accountIDs";
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
    public Account getAccount(String accountID) {
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
                    account = new SavingsAccount(accountType, balance, Float.parseFloat(respArgs[3]));
                else if (Objects.equals(accountType, "credit"))
                    account = new CreditAccount(accountType, balance, Float.parseFloat(respArgs[3]));
                else
                    account = new Account(accountType, balance);
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
    public void getAccounts() {
        // get the user's account IDs
        String[] accountIDs = this.getAccountIDs();

        // clear the user's list of accounts
        accounts = new ArrayList<>();

        // fetch and store information for each account
        for (String accountID : accountIDs) {
            Account account = this.getAccount(accountID);
            accounts.add(account);
        }
    }

    // Loads the create customer scene.
    public void loadCreateCustomer() throws IOException {
        // load the create account scene
        Parent root = FXMLLoader.load(getClass().getResource("create_customer.fxml"));
        Stage stage = (Stage)this.btnCreateAccount.getScene().getWindow();
        stage.setScene(new Scene(root, 720, 480));
    }
    
    // Attempts to create a new customer and account on the server.
    public void createCustomer() throws IOException {
//        Request: customer | create | [fname] | [lname] | [email] | [ssn] | [dob] | [address] | [phone] | password
//        Response: {success/fail} | errorMsg

        // get the values inputted into the text fields
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        ssn = txtSSN.getText();
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
                // send create account request to the server
                this.createAccount();
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
    public void createAccount() throws IOException {
//        Request: account | create | [customerSsn] | [accountType]
//        Response: {success/fail} | {accountID/errorMsg}

        // get the value inputted into the data field
        String accountType = this.selectedAccountType;

        // send create customer request to the server and receive server's response.
        String cmd = String.format("account|create|%s|%s", ssn, accountType);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        Alert alert;
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // get the new account's ID
                String accountID = respArgs[1];

                // set the account ID as the current account ID
                currentAccountID = accountID;

                // get the new account's information
                Account newAccount = this.getAccount(accountID);

                // add the new account to the user's list of accounts
                accounts.add(newAccount);

                // load the main account scene based on account type
                String fxml = "";
                switch (newAccount.getType()) {
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
                Parent root = FXMLLoader.load(getClass().getResource(fxml));
                Stage stage = (Stage)this.btnCreateAccount.getScene().getWindow();
                stage.setScene(new Scene(root, 720, 480));

                // display success alert
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Successfully created account.", ButtonType.OK);
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
