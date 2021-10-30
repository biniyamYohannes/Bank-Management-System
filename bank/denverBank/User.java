package bank.denverBank;

public abstract class User {

    private String fName;
    private String lName;
    private String Email;

    public User(String fName, String lName, String email) {
        this.fName = fName;
        this.lName = lName;
        Email = email;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
