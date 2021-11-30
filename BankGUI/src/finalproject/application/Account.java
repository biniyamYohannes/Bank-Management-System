package finalproject.application;

public class Account {
    private final String type;
    private final float balance;

    public Account(String type, float balance) {
        this.type = type;
        this.balance = balance;
    }

    public String getType() { return this.type; }
    public float getBalance() { return this.balance; }
}
