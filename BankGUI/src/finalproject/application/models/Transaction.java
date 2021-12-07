package finalproject.application.models;

import java.time.LocalDate;
import java.lang.Math;

public class Transaction {
    private final LocalDate date;
    private final float amount;

    public Transaction(LocalDate date, float amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() { return this.date; }
    public float getAmount() { return this.amount; }

    @Override
    public String toString()
    {
        return String.format("%s - %s$%.2f",
                this.date,
                this.amount < 0 ? "Withdrew " : "Deposited ",
                Math.abs(this.amount));
    }
}
