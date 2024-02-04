package bg.sofia.uni.fmi.mjt.splitwise.server.model;

public class Obligation {

    private final User firstUser;
    private final User secondUser;
    private double balance;

    public Obligation(User first, User second, double balance) {
        this.firstUser = first;
        this.secondUser = second;
        this.balance = balance;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
