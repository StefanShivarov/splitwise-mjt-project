package bg.sofia.uni.fmi.mjt.splitwise.server.model;

public class User {

    private static long currentId = 1;
    private final long id;
    private final String username;
    private final String hashedPass;

    public User(String username, String hashedPass) {
        this.username = username;
        this.hashedPass = hashedPass;
        this.id = currentId++;
    }

    public User(long id, String username, String hashedPass) {
        this.id = id;
        this.username = username;
        this.hashedPass = hashedPass;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String hashedPass) {
        return this.hashedPass.equals(hashedPass);
    }

}
