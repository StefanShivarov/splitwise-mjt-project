package bg.sofia.uni.fmi.mjt.splitwise.server.model;

public class User {

    private final String username;
    private final String hashedPass;

    public User(String username, String hashedPass) {
        this.username = username;
        this.hashedPass = hashedPass;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPass;
    }

}
