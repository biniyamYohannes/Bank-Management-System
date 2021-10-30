package bank.denverBank;

public class CreditCard extends Account{
    private float creditLimit;

    public CreditCard(String accountId, int accountType, float balance, float creditLimit) {
        super(accountId, 3, balance);
        this.creditLimit = creditLimit;
    }

    public float getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void makeAPayment(float paymentAmount) {
        this.setBalance(this.getBalance() - paymentAmount);
    }
}




