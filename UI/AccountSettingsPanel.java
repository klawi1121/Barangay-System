package UI;

import user_management.*;
import data_storage_security.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class AccountSettingsPanel extends JPanel {
    private User settingsUser;

    public AccountSettingsPanel(User user) {
        this.settingsUser = user;

        setLayout(new BorderLayout());
        setBackground(BarangayColors.LIGHT_BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Account Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel userCard = new JPanel(new BorderLayout());
        userCard.setBackground(Color.WHITE);
        userCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JPanel userInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        userInfoPanel.setBackground(Color.WHITE);

        addUserInfoRow(userInfoPanel, "Username:", settingsUser.getUsername());
        addUserInfoRow(userInfoPanel, "Role:", settingsUser.getRole());
        addUserInfoRow(userInfoPanel, "Email:", settingsUser.getEmail());
        addUserInfoRow(userInfoPanel, "Phone:", settingsUser.getPhone());
        addUserInfoRow(userInfoPanel, "Status:", settingsUser.isActive() ? "Active" : "Inactive");

        userCard.add(userInfoPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 0, 50));

        StyledButton changePassBtn = new StyledButton("Change Password",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        changePassBtn.setPreferredSize(new Dimension(250, 40));
        changePassBtn.addActionListener(e -> changePassword());

        StyledButton viewLogsBtn = new StyledButton("View Login History",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        viewLogsBtn.setPreferredSize(new Dimension(250, 40));
        viewLogsBtn.addActionListener(e -> viewLoginHistory());

        actionsPanel.add(changePassBtn);
        actionsPanel.add(viewLogsBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(userCard, BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addUserInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(BarangayColors.TEXT_COLOR);
        panel.add(lbl);

        JTextField field = new JTextField(value);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setEditable(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setBackground(BarangayColors.LIGHT_BACKGROUND);
        panel.add(field);
    }

    private void changePassword() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField currentPass = new StyledPasswordField(15);
        currentPass.putClientProperty("JTextField.placeholderText", "Enter current password");
        panel.add(currentPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPass = new StyledPasswordField(15);
        newPass.putClientProperty("JTextField.placeholderText", "Enter new password (6+ characters)");
        panel.add(newPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPass = new StyledPasswordField(15);
        confirmPass.putClientProperty("JTextField.placeholderText", "Confirm new password");
        panel.add(confirmPass, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String current = new String(currentPass.getPassword());
            String newP = new String(newPass.getPassword());
            String confirm = new String(confirmPass.getPassword());

            if (!settingsUser.verifyPassword(current)) {
                JOptionPane.showMessageDialog(this,
                        "Current password is incorrect!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newP.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "New password must be at least 6 characters long!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newP.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Password cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newP.equals(confirm)) {
                JOptionPane.showMessageDialog(this,
                        "New passwords don't match!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Password changed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            SecureFileHandler.logActivity(settingsUser.getUsername(), "PASSWORD_CHANGED");
        }
    }

    private void viewLoginHistory() {
        List<String> logs = SecureFileHandler.getLoginHistory(settingsUser.getUsername());

        if (logs.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No login history found.",
                    "Login History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog historyDialog = new JDialog(frame, "Login History", true);
        historyDialog.setSize(600, 400);
        historyDialog.setLocationRelativeTo(frame);
        historyDialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Login History for " + settingsUser.getUsername());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);

        String[] columns = { "Timestamp", "Action" };
        DefaultTableModel logModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int count = 0;
        for (int i = logs.size() - 1; i >= 0 && count < 50; i--) {
            String log = logs.get(i);
            String[] parts = log.split("\\|");
            if (parts.length >= 2) {
                String timestamp = parts[0];
                String action = parts.length > 2 ? parts[2] : parts[1];
                logModel.addRow(new Object[] { timestamp, action });
                count++;
            }
        }

        JTable logTable = new StyledTable(logModel);
        logTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(logTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        StyledButton closeButton = new StyledButton("Close",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        closeButton.addActionListener(e -> historyDialog.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        historyDialog.add(mainPanel);
        historyDialog.setVisible(true);
    }
}
