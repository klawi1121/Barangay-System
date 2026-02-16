import javax.swing.*;
import java.awt.*;
import UI.*;
import user_management.*;
import java.util.List;

public class BarangayResidentsSystem {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private User currentUser;
    private List<Resident> residents;
    private List<User> users;
    
    private LoginPanel loginPanel;
    private OTPScreen otpScreen;
    private MainDashboard dashboard;
    
    public BarangayResidentsSystem() {
        initializeData();
        initializeUI();
    }
    
    private void initializeData() {
        residents = SecureFileHandler.loadResidents();
        users = SecureFileHandler.loadUsers();
    }
    
    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Barangay Residents Record System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BarangayColors.LIGHT_BACKGROUND);
        
        loginPanel = new LoginPanel();
        otpScreen = new OTPScreen();
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(otpScreen, "OTP");
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
}