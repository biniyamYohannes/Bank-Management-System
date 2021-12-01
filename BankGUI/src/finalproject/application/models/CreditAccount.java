package finalproject.application.models;

public class CreditAccount extends Account {
    private final float limit;

    public CreditAccount(String id, String type, float balance, float limit) {
        super(id, type, balance);
        this.limit = limit;
    }

    public float getLimit() { return this.limit; }
}
