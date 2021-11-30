package finalproject.application;

public class SavingsAccount extends Account{
    private final float interest;

    public SavingsAccount(String type, float balance, float interest) {
        super(type, balance);
        this.interest = interest;
    }

    public float getInterest() { return this.interest; }
}
