package finalproject.application;

public class CreditAccount extends Account {
    private final float limit;

    public CreditAccount(String type, float balance, float limit) {
        super(type, balance);
        this.limit = limit;
    }

    public float getLimit() { return this.limit; }
}
