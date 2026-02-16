package user_management;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class User {

    public enum UserRole {
        ADMIN,
        STAFF
    }

    protected String username;
    protected String encryptedPassword;
    protected String role;
    protected String email;
    protected String phone;
    protected boolean isActive;
    
    public User(String username, String password, String role, String email, String phone) {
        this.username = username;
        this.encryptedPassword = encryptPassword(password);
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.isActive = true;
    }

    public abstract boolean canAccessAdminPanel();
    public abstract boolean canManageUsers();
    public abstract boolean canViewAllResidents();
    
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }
    
    public boolean verifyPassword(String password) {
        return this.encryptedPassword.equals(encryptPassword(password));
    }
    
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}