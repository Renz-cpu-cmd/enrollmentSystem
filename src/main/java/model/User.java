package model;
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    // constructors, getters, setters
    public User() {}
    public User(String username, String passwordHash, String role) {
        this.username = username; this.passwordHash = passwordHash; this.role = role;
    }
    // getters/setters omitted for brevity
}
