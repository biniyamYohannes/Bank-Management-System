package bank.denverBank;

import java.time.LocalDate;
import java.util.ArrayList;

public class Customer extends User{
    private String ssn;
    private LocalDate dob;
    private String address;
    private String phoneNum;
    private ArrayList<Account> accounts;

    public Customer(String fName, String lName, String email, String ssn, LocalDate dob, String address, String phoneNum) {
        super(fName, lName, email);
        this.ssn = ssn;
        this.dob = dob;
        this.address = address;
        this.phoneNum = phoneNum;
        this. accounts = new ArrayList<>();
    }

    public String getLastFour() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }
}
