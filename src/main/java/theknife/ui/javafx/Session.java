package it.unininsubria.theknifeui.ui.javafx;

public class Session {

    public enum Role {
        CLIENTE,
        RISTORATORE,
        GUEST
    }

    private static Session instance;

    private String username;
    private Role role;

    private Session() {
        this.role = Role.GUEST;
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void login(String username, Role role) {
        this.username = username;
        this.role = (role == null ? Role.GUEST : role);
    }

    public void logout() {
        this.username = null;
        this.role = Role.GUEST;
    }

    public boolean isGuest() {
        return role == Role.GUEST;
    }

    public boolean isAuthenticated() {
        return role != Role.GUEST;
    }

    public void setAuthenticated(boolean authenticated) {
        if (!authenticated) {
            logout();
        } else {
            if (this.role == Role.GUEST) {
                this.role = Role.CLIENTE;
            }
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // nuovo: permette di aggiornare solo il ruolo
    public void setRole(Role role) {
        this.role = (role == null ? Role.GUEST : role);
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}