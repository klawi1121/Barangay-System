package UI;

import data_storage_security.SecureFileHandler;
import data_model.Resident;
import user_management.Admin;
import user_management.User;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class OTPScreen extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final List<Resident> residents;
    private final List<User> users;
    private User currentUser;

    private StyledTextField otpField;
    private JLabel otpLabel;

    private Admin admin;

    public OTPScreen(CardLayout cardLayout, JPanel mainPanel, List<Resident> residents, List<User> users) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.residents = residents;
        this.users = users;

        setLayout(new GridBagLayout());
        setBackground(BarangayColors.LIGHT_BACKGROUND);

        JPanel otpPanel = new JPanel();
        otpPanel.setLayout(new BoxLayout(otpPanel, BoxLayout.Y_AXIS));
        otpPanel.setBackground(Color.WHITE);
        otpPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        JLabel titleLabel = new JLabel("Two-Factor Authentication");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        otpLabel = new JLabel(
                "<html><div style='text-align: center;'>OTP has been generated.<br>Valid for 5 minutes</div></html>");
        otpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        otpLabel.setForeground(BarangayColors.TEXT_COLOR);
        otpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel otpInputLabel = new JLabel("Enter 6-digit OTP:");
        otpInputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        otpInputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        otpField = new StyledTextField(6);
        otpField.setFont(new Font("Segoe UI", Font.BOLD, 20));
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setMaximumSize(new Dimension(200, 40));
        otpField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        StyledButton verifyButton = new StyledButton("Verify OTP",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        verifyButton.setPreferredSize(new Dimension(120, 35));
        verifyButton.addActionListener(e -> verifyOTP());

        StyledButton resendButton = new StyledButton("Resend OTP",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        resendButton.setPreferredSize(new Dimension(120, 35));
        resendButton.addActionListener(e -> resendOTP());

        buttonPanel.add(verifyButton);
        buttonPanel.add(resendButton);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(Color.WHITE);

        StyledButton backButton = new StyledButton("Back to Login",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        backPanel.add(backButton);

        otpPanel.add(titleLabel);
        otpPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        otpPanel.add(otpLabel);
        otpPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        otpPanel.add(otpInputLabel);
        otpPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        otpPanel.add(otpField);
        otpPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        otpPanel.add(buttonPanel);
        otpPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        otpPanel.add(backPanel);

        add(otpPanel);
    }

    // DEV: show OTP on screen
    public void setOTP(String otp, Admin admin, User currentUser) {
        this.admin = admin;
        this.currentUser = currentUser;

        otpLabel.setText("<html><div style='text-align: center;'>OTP Generated: <b>"
                + otp + "</b><br>Valid for 5 minutes</div></html>");

        otpField.setText("");
        otpField.requestFocus();
    }

    private void verifyOTP() {
        String otp = otpField.getText().trim();
        if (otp.length() != 6) {
            JOptionPane.showMessageDialog(this, "OTP must be 6 digits!",
                    "Invalid OTP", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (admin != null && admin.verifyOTP(otp)) {
            SecureFileHandler.logActivity(admin.getUsername(), "OTP_VERIFIED");
            admin.incrementLoginCount();
            showDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid or expired OTP!",
                    "Verification Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resendOTP() {
        if (admin == null)
            return;

        String newOTP = admin.generateOTP();

        // DEV: show new OTP too
        otpLabel.setText("<html><div style='text-align: center;'>OTP Resent: <b>"
                + newOTP + "</b><br>Valid for 5 minutes</div></html>");

        otpField.setText("");
        otpField.requestFocus();

        SecureFileHandler.logActivity(admin.getUsername(), "OTP_RESENT");
    }

    private void showDashboard() {
        MainDashboard dashboard = new MainDashboard(currentUser, residents, users);
        mainPanel.add(dashboard, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
