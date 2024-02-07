package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Obligation that = (Obligation) o;
        return Double.compare(balance, that.balance) == 0
                && Objects.equals(firstUser, that.firstUser)
                && Objects.equals(secondUser, that.secondUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser, balance);
    }

}
