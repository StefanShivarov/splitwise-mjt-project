package bg.sofia.uni.fmi.mjt.splitwise.server.model;

public class User {

    private String firstName;
    private String lastName;
    private final String username;
    private final String hashedPassword;

    public User(String username, String hashedPass, String firstName, String lastName) {
        this.username = username;
        this.hashedPassword = hashedPass;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String username, String hashedPass) {
        this(username, hashedPass, "", "");
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        boolean blankNames = firstName.isBlank() && lastName.isBlank();
        return String.format("%s%s%s",
                firstName.isBlank() ? "" : firstName + " ",
                lastName.isBlank() ? "" : lastName + " ",
                blankNames ? username : "(" + username + ")");
    }

    public String getFullName() {
        if (firstName.isBlank() || lastName.isBlank()) {
            return username;
        }

        return String.format("%s %s",
                firstName,
                lastName);
    }

}
