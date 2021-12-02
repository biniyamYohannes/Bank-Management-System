package finalproject.application.models;

public class SavingsAccount extends Account {
    private final float interest;

    public SavingsAccount(String id, String type, float balance, float interest) {
        super(id, type, balance);
        this.interest = interest;
    }

    public float getInterest() { return this.interest; }
}
