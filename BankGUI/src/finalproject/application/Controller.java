package finalproject.application;

import finalproject.application.models.Account;
import finalproject.application.models.CreditAccount;
import finalproject.application.models.SavingsAccount;
import finalproject.application.models.Transaction;
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

public class Controller {

    // client and account data for current session
    private static final Client client = new Client();
    private static String firstName;
    private static String lastName;
    private static String email;
    private static Account currentAccount;
    private static final ArrayList<Account> accounts = new ArrayList<>();

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
    public TextField txtAmount;
    public Label labelFirstName;
    public Label labelAccount;
    public ListView<Account> listAccounts;
    public ListView<Transaction> listTransactions;
    public Button btnCreateAccount;
    public Button btnLogin;
    public Button btnAddAccount;
    public Button btnBack;
    public Button btnSelectAccount;
    public Button btnWithdraw;
    public Button btnDeposit;
    public Button btnPay;
    public Button btnLogout;

    public Controller() {
        if (!client.isConnected())
            client.connect();

        this.labelFirstName = new Label();
        this.labelAccount = new Label();
        this.listAccounts = new ListView<>();
        this.listTransactions = new ListView<>();
    }

    public void initialize() {
        
        // initialize GUI components for user's first name and list of accounts
        this.labelFirstName.setText(firstName);
        this.listAccounts.setItems(FXCollections.observableArrayList(accounts));

        // return here if a user account is not selected yet
        if (currentAccount == null)
            return;

        // initialize GUI component for account name
        this.labelAccount.setText(currentAccount.toString());

        // initialize GUI component for transactions
        this.listTransactions.setItems(FXCollections.observableArrayList(currentAccount.getTransactions()));
    }

// Scene handlers ------------------------------------------------------------------------------------------------------

    // Gets the stage associated with an ActionEvent.
    private Stage getStage(ActionEvent actionEvent) {
        Node node = (Node)actionEvent.getSource();
        return (Stage)node.getScene().getWindow();
    }

    // Loads a scene from an FXML file.
    private void loadScene(ActionEvent actionEvent, String fxml) throws IOException {
        Stage stage = this.getStage(actionEvent);
        Parent root = FXMLLoader.load(getClass().getResource("views/" + fxml));
        stage.setScene(new Scene(root, 720, 480));
    }

    // Loads the login scene.
    public void loadLogin(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "login.fxml");
    }

    // Loads the create customer scene.
    public void loadCreateCustomer(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "create_customer.fxml");
    }

    // Loads the add account scene.
    public void loadAddAccount(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "add_account.fxml");
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

    // Loads the account selection scene.
    public void loadAccountSelection(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "account_selection.fxml");
    }

    // Handles the event that the user clicks on an account in the account list by setting it as the current account.
    public void selectAccount(MouseEvent mouseEvent) {
        currentAccount = this.listAccounts.getSelectionModel().getSelectedItem();
    }

    // Loads the main account scene.
    public void loadAccountMain(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, currentAccount.getType() + "_main.fxml");
    }

    // Loads the account withdrawal scene.
    public void loadWithdraw(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "account_withdraw.fxml");
    }

    // Loads the account deposit scene.
    public void loadDeposit(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "account_deposit.fxml");
    }

    // Loads the credit payment scene.
    public void loadPayment(ActionEvent actionEvent) throws IOException {
        this.loadScene(actionEvent, "credit_payment.fxml");
    }

// Client-server communication handlers --------------------------------------------------------------------------------

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

    // Displays a success alert.
    private void successAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
        alert.show();
    }

    // Displays a failure alert.
    private void failAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.show();
    }

    // Attempts to log into an account on the server with the provided credentials.
    public void login(ActionEvent actionEvent) throws IOException {

        // get the values inputted into the text fields
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // send login request to the server and receive server's response.
        String cmd = String.format("login|%s|%s", email, password);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // set the current user's info
                Controller.firstName = respArgs[1];
                Controller.lastName = respArgs[2];
                Controller.email = respArgs[3];

                // load the user's accounts
                this.getAccounts();

                // load the account selection scene
                this.loadAccountSelection(actionEvent);
                break;

            case "fail":
                this.failAlert(respArgs[1]);

//                firstName= "Andy";
//                lastName = "Le";
//                Transaction trans1 = new Transaction(LocalDate.of(2017, 1, 13), 100);
//                Transaction trans2 = new Transaction(LocalDate.of(2017, 2, 13), -50);
//                ArrayList<Transaction> transactions = new ArrayList<>();
//                transactions.add(trans1);
//                transactions.add(trans2);
//                Account savings = new SavingsAccount("1", "savings", 200, transactions, 2);
//                accounts.add(savings);
//                this.loadAccountSelection(actionEvent);

                break;
        }
    }

    // Fetches and returns the user's account IDs from the server.
    private String[] getAccountIDs() {

        String[] accountIDs;

        // send account IDs request to the server and receive server's response.
        String cmd = "customer|get|all";
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // get the account IDs
                accountIDs = Arrays.copyOfRange(respArgs, 1, respArgs.length);
                break;

            case "fail":
                accountIDs = new String[]{};
                this.failAlert(respArgs[1]);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + respArgs[0]);
        }

        return accountIDs;
    }

    // Requests an account's information from the server, returns an Account object containing the account info.
    private Account getAccount(String accountID) {

        // send create customer request to the server and receive server's response.
        String cmd = String.format("account|get|%s", accountID);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        Account account = null;
        switch (respArgs[0]) {
            case "success":
                // get the server's responses
                float balance = Float.parseFloat(respArgs[1]);
                String accountType = respArgs[2];

                // get the account's transactions
                ArrayList<Transaction> transactions = this.getTransactions(accountID);

                // create account depending on type
                switch (accountType) {
                    case "savings":
                        float interest = Float.parseFloat(respArgs[3]);
                        account = new SavingsAccount(accountID, accountType, balance, transactions, interest);
                        break;
                    case "credit":
                        float limit = Float.parseFloat(respArgs[3]);
                        account = new CreditAccount(accountID, accountType, balance, transactions, limit);
                        break;
                    default:
                        account = new Account(accountID, accountType, balance, transactions);
                }
                break;

            case "fail":
                // display failure alert with message from server
                this.failAlert(respArgs[1]);
                break;
        }

        return account;
    }

    // Fetches an account's transactions from the server. Returns the transactions as an ArrayList.
    private ArrayList<Transaction> getTransactions(String accountID) {

        ArrayList<Transaction> transactions = new ArrayList<>();

        // send account transactions request to the server and receive server's response.
        String cmd = String.format("transaction|get|%s", accountID);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // get string representations of transactions from server
                for (String transactionStr : Arrays.copyOfRange(respArgs, 1, respArgs.length)) {

                    // split each string representation and parse into date and amount
                    String[] transactionArgs = transactionStr.split(",");
                    LocalDate date = LocalDate.parse(transactionArgs[0]);
                    float amount = Float.parseFloat(transactionArgs[1]);

                    // add the transaction
                    transactions.add(new Transaction(date, amount));
                }
                break;

            case "fail":
                this.failAlert(respArgs[1]);
                break;
        }

        return transactions;
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
    }
    
    // Attempts to create a new customer and account on the server.
    public void createCustomer(ActionEvent actionEvent) throws IOException {
        // Request: customer | create | [fname] | [lname] | [email] | [ssn] | [dob] | [address] | [phone] | password
        // Response: {success/fail} | errorMsg

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
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // set the current user's info
                Controller.firstName = firstName;
                Controller.lastName = lastName;
                Controller.email = email;

                // send create account request to the server
                this.createAccount(actionEvent);
                break;

            case "fail":
                this.failAlert(respArgs[1]);
                break;
        }
    }

    // Attempts to create a new account on the server.
    public void createAccount(ActionEvent actionEvent) throws IOException {
        // Request: account | create | [email] | [accountType]
        // Response: {success/fail} | {accountID/errorMsg}

        // send create customer request to the server and receive server's response
        String cmd = String.format("account|create|%s|%s", email, this.selectedAccountType);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // get the new account's ID
                String accountID = respArgs[1];

                // get the new account's information
                Account newAccount = this.getAccount(accountID);

                // set the new account as the current account
                currentAccount = newAccount;

                // add the new account to the user's list of accounts
                accounts.add(newAccount);

                // load the main account scene
                this.loadAccountMain(actionEvent);

                // display success alert
                this.successAlert("Account successfully created.");
                break;

            case "fail":
                this.failAlert(respArgs[1]);
                break;
        }
    }

    // Sends an account transaction request to the server.
    public void transact(ActionEvent actionEvent) throws IOException {

        // get the user-inputted amount to transact
        float amount = Float.parseFloat(this.txtAmount.getText());

        // change the amount to negative if the transaction is a withdrawal
        if (actionEvent.getSource() == this.btnWithdraw)
            amount *= -1;

        // send transaction request to the server and receive server's response.
        String cmd = String.format("transaction|put|%s|%f", currentAccount.getID(), amount);
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                currentAccount.addTransaction(new Transaction(LocalDate.now(), amount));
                this.loadAccountMain(actionEvent);
                this.successAlert("Transaction successful.");
                break;

            case "fail":
                this.failAlert(respArgs[1]);
                break;
        }
    }

    // Sends a logout request to the server.
    public void logout(ActionEvent actionEvent) throws IOException {

        // send logout request to the server and receive server's response.
        String cmd = "logout";
        String response = sendCommand(cmd);

        // perform actions based on server's response
        String[] respArgs = response.split("\\|");
        switch (respArgs[0]) {
            case "success":
                // clear the current user's info
                firstName = "";
                lastName = "";
                email = "";
                currentAccount = null;
                accounts.clear();

                // load the login scene
                this.loadLogin(actionEvent);
                break;

            case "fail":
                this.failAlert(respArgs[1]);
                break;
        }
    }

    public void exitApplication(ActionEvent event) {

    }
}
