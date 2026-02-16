package UI;

import UI.*;
import data_storage_security.*;
import user_management.*;
import data_model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OTPScreen extends JPanel {
private StyledTextField otpField;
        private JLabel otpLabel;
        private Admin admin;
        
        public OTPScreen() {
            setLayout(new GridBagLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            JPanel otpPanel = new JPanel();
            otpPanel.setLayout(new BoxLayout(otpPanel, BoxLayout.Y_AXIS));
            otpPanel.setBackground(Color.WHITE);
            otpPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
            ));
            
            JLabel titleLabel = new JLabel("Two-Factor Authentication");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            otpLabel = new JLabel("<html><div style='text-align: center;'>OTP has been generated.<br>Check your email/phone.</div></html>");
            otpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            otpLabel.setForeground(BarangayColors.TEXT_COLOR);
            otpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPanel otpInputPanel = new JPanel();
            otpInputPanel.setLayout(new BoxLayout(otpInputPanel, BoxLayout.Y_AXIS));
            otpInputPanel.setBackground(Color.WHITE);
            otpInputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel otpInputLabel = new JLabel("Enter 6-digit OTP:");
            otpInputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            otpInputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            otpField = new StyledTextField(6);
            otpField.setFont(new Font("Segoe UI", Font.BOLD, 20));
            otpField.setHorizontalAlignment(JTextField.CENTER);
            otpField.setMaximumSize(new Dimension(200, 40));
            otpField.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
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
            
            JPanel backPanel = new JPanel();
            backPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
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
        
        public void setOTP(String otp, Admin admin) {
            this.admin = admin;
            otpLabel.setText("<html><div style='text-align: center;'>OTP Generated: <b>" + otp + "</b><br>Valid for 5 minutes</div></html>");
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
            
            if (admin.verifyOTP(otp)) {
                SecureFileHandler.logActivity(admin.getUsername(), "OTP_VERIFIED");
                admin.incrementLoginCount();
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid or expired OTP!", 
                    "Verification Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void resendOTP() {
            String newOTP = admin.generateOTP();
            setOTP(newOTP, admin);
            SecureFileHandler.logActivity(admin.getUsername(), "OTP_RESENT");
        }
    }
    
    private void showDashboard() {
        dashboard = new MainDashboard(currentUser, residents, users);
        mainPanel.add(dashboard, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
        frame.revalidate();
    }