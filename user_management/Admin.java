package user_management;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class Admin extends User {
    private int otpAttempts;
    private String currentOTP;
    private LocalDateTime otpExpiry;
    private static final int MAX_OTP_ATTEMPTS = 3;
    private static final int OTP_VALID_MINUTES = 5;
    private int loginCount;
    
    public Admin(String username, String password, String email, String phone) {
        super(username, password, "ADMIN", email, phone);
        this.otpAttempts = 0;
        this.loginCount = 0;
    }
    
    @Override
    public boolean canAccessAdminPanel() { return true; }
    @Override
    public boolean canManageUsers() { return true; }
    @Override
    public boolean canViewAllResidents() { return true; }
    
    public boolean requiresOTP() {
        return loginCount % 5 == 0 || otpAttempts >= MAX_OTP_ATTEMPTS;
    }
    
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        currentOTP = String.valueOf(otp);
        otpExpiry = LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES);
        otpAttempts = 0;
        return currentOTP;
    }
    
    public boolean verifyOTP(String otp) {
        if (currentOTP == null || otpExpiry == null) return false;
        if (LocalDateTime.now().isAfter(otpExpiry)) {
            currentOTP = null;
            return false;
        }
        if (currentOTP.equals(otp)) {
            loginCount++;
            currentOTP = null;
            otpAttempts = 0;
            return true;
        } else {
            otpAttempts++;
            if (otpAttempts >= MAX_OTP_ATTEMPTS) currentOTP = null;
            return false;
        }
    }
    
    public void incrementLoginCount() {
        loginCount++;
    }
}
