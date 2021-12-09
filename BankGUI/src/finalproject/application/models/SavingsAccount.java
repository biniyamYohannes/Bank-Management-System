package finalproject.application.models;

import java.util.ArrayList;

public class SavingsAccount extends Account {
    private final float interest;

    public SavingsAccount(String id, String type, float balance, ArrayList<Transaction> transactions, float interest) {
        super(id, type, balance, transactions);
        this.interest = interest;
    }

    public float getInterest() { return this.interest; }

    @Override
    public String toString()
    {
        return String.format("%s - Interest: %.2f%%", super.toString(), this.interest);
    }
}
