package finalproject.application.models;

import java.util.ArrayList;

public class CreditAccount extends Account {
    private final float limit;

    public CreditAccount(String id, String type, float balance, ArrayList<Transaction> transactions, float limit) {
        super(id, type, balance, transactions);
        this.limit = limit;
    }

    public float getLimit() { return this.limit; }

    @Override
    public float getBalance() {
        // return the account's remaining credit
        return this.limit - super.getBalance();
    }

    @Override
    public String toString()
    {
        return String.format("%s | Limit: $%.2f", super.toString(), this.limit);
    }
}
