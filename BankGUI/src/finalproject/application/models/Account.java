package finalproject.application.models;

import java.util.ArrayList;

public class Account {
    private final String id;
    private final String type;
    private float balance;
    private ArrayList<Transaction> transactions;

    public Account(String id, String type, float balance, ArrayList<Transaction> transactions) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getID() { return this.id; }

    public String getType() { return this.type; }

    public float getBalance() { return this.balance; }

    public ArrayList<Transaction> getTransactions() {
        // sort transactions from most recent first
        this.transactions.sort(new TransactionSorter());
        return this.transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.balance += transaction.getAmount();
    }

    public void update(Account account) {
        this.balance = account.balance;
        this.transactions = account.transactions;
    }

    @Override
    public String toString()
    {
        return String.format("#%s (%s) | Current Balance: $%.2f",
                this.id, this.type, this.getBalance());
    }
}


