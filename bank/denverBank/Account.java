package bank.denverBank;

public abstract class Account {
    private String  accountId;
    private int accountType;
    private float balance;

    public Account(String accountId, int accountType, float balance) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAccountType() {
        return accountType;
    }


    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance += balance;
    }
}
