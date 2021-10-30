package bank.denverBank;

public class Savings extends Account {
    private float interestRate;

    public Savings(String accountId, int accountType, float balance, float  interestRate) {
        super(accountId, 2, balance);
        this.interestRate = interestRate;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }
}
