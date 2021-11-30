package finalproject.application;

public class Account {
    private final String id;
    private final String type;
    private float balance;

    public Account(String id, String type, float balance) {
        this.id = id;
        this.type = type;
        this.balance = balance;
    }

    public String getType() { return this.type; }
    public float getBalance() { return this.balance; }
    public void setBalance(float balance) { this.balance = balance; }

    @Override
    public String toString()
    {
        return String.format("#%s (%s)", this.id, this.type);
    }
}
