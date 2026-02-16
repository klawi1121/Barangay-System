import javax.swing.*;
import java.awt.*;
import java.util.List;

import UI.BarangayColors;
import UI.LoginPanel;
import UI.MainDashboard;
import UI.OTPScreen;

import data_model.Resident;
import data_storage_security.SecureFileHandler;
import user_management.User;

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

        // LoginPanel mo currently default constructor pa, so di natin gagalawin
        otpScreen = new OTPScreen(cardLayout, mainPanel, residents, users);

        loginPanel = new LoginPanel(frame, mainPanel, cardLayout, otpScreen, users, residents);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(otpScreen, "OTP");

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BarangayResidentsSystem::new);
    }
}
