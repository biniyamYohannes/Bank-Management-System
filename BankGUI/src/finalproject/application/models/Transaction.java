package finalproject.application.models;

import java.lang.Math;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final LocalDateTime dateTime;
    private final float amount;

    public Transaction(LocalDateTime dateTime, float amount) {
        this.dateTime = dateTime;
        this.amount = amount;
    }

    public LocalDateTime getDateTime() { return this.dateTime; }
    public float getAmount() { return this.amount; }

    @Override
    public String toString()
    {
        return String.format("%s (%s$%.2f)",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime),
                this.amount < 0 ? "-" : "+",
                Math.abs(this.amount));
    }
}