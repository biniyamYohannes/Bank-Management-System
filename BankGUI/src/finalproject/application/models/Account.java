package finalproject.application.models;

import java.util.ArrayList;

public class Account {
    private final String id;
    private final String type;
    private float balance;
    private final ArrayList<Transaction> transactions;

    public Account(String id, String type, float balance) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getType() { return this.type; }
    public float getBalance() { return this.balance; }
    public void setBalance(float balance) { this.balance = balance; }
    public ArrayList<Transaction> getTransactions() { return this.transactions; }
    public void addTransaction(Transaction transaction) { this.transactions.add(transaction); }

    @Override
    public String toString()
    {
        return String.format("#%s (%s) - Current Balance: $%.2f", this.id, this.type, this.balance);
    }
}
