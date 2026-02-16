package UI;

import user_management.*;
import data_storage_security.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class LoginPanel extends JPanel {

    private final JFrame frame;
    private final List<User> users;

    private StyledTextField usernameField;
    private StyledPasswordField passwordField;
    private StyledComboBox<String> roleComboBox;

    private User currentUser;

    public LoginPanel(JFrame frame, List<User> users) {
        this.frame = frame;
        this.users = users;

        setLayout(new GridBagLayout());
        setBackground(BarangayColors.LIGHT_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Barangay Records System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Local Government Information System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(BarangayColors.TEXT_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(titlePanel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new StyledTextField(20);
        usernameField.putClientProperty("JTextField.placeholderText", "Enter username");
        cardPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new StyledPasswordField(20);
        passwordField.putClientProperty("JTextField.placeholderText", "Enter password");
        cardPanel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        roleComboBox = new StyledComboBox<>(new String[]{"ADMIN", "STAFF", "RESIDENT"});
        cardPanel.add(roleComboBox, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);

        StyledButton loginButton = new StyledButton("Login",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.addActionListener(e -> attemptLogin());
        cardPanel.add(loginButton, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);

        StyledButton createAccountButton = new StyledButton("Create New Account",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        createAccountButton.setPreferredSize(new Dimension(200, 40));
        createAccountButton.addActionListener(e -> showCreateAccountDialog());
        cardPanel.add(createAccountButton, gbc);

        add(cardPanel);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getRole().equals(role) &&
                    user.isActive() &&
                    user.verifyPassword(password)) {

                currentUser = user;
                SecureFileHandler.logActivity(username, "LOGIN_ATTEMPT_SUCCESS");

                JOptionPane.showMessageDialog(this,
                        "Login successful! Role: " + user.getRole(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // TODO: Navigate to dashboard using your CardLayout/AppFrames
                return;
            }
        }

        JOptionPane.showMessageDialog(this,
                "Invalid credentials or inactive account!",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        SecureFileHandler.logActivity(username, "LOGIN_ATTEMPT_FAILED");
    }

    private void showCreateAccountDialog() {
        JDialog dialog = new JDialog(frame, "Create New Account", true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Username:"));
        StyledTextField newUserField = new StyledTextField(15);
        newUserField.putClientProperty("JTextField.placeholderText", "Enter username");
        formPanel.add(newUserField);

        formPanel.add(new JLabel("Password:"));
        StyledPasswordField newPassField = new StyledPasswordField(15);
        newPassField.putClientProperty("JTextField.placeholderText", "6+ characters");
        formPanel.add(newPassField);

        formPanel.add(new JLabel("Confirm Password:"));
        StyledPasswordField confirmPassField = new StyledPasswordField(15);
        confirmPassField.putClientProperty("JTextField.placeholderText", "Confirm password");
        formPanel.add(confirmPassField);

        formPanel.add(new JLabel("Email:"));
        StyledTextField emailField = new StyledTextField(15);
        emailField.putClientProperty("JTextField.placeholderText", "email@example.com");
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        StyledTextField phoneField = new StyledTextField(15);
        phoneField.putClientProperty("JTextField.placeholderText", "09xxxxxxxxx or +63xxxxxxxxx");
        phoneField.setDocument(new PhoneDocument());
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Role:"));
        StyledComboBox<String> roleBox = new StyledComboBox<>(new String[]{"RESIDENT", "STAFF"});
        formPanel.add(roleBox);

        formPanel.add(new JLabel("Resident ID:"));
        StyledTextField residentIdField = new StyledTextField(15);
        residentIdField.putClientProperty("JTextField.placeholderText", "For resident accounts only");
        formPanel.add(residentIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        StyledButton createButton = new StyledButton("Create Account",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        createButton.setPreferredSize(new Dimension(140, 35));
        createButton.addActionListener(e -> {
            String password = new String(newPassField.getPassword());
            String confirm = new String(confirmPassField.getPassword());
            String phone = phoneField.getText().trim();

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(dialog, "Passwords don't match!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = newUserField.getText().trim();
            String role = (String) roleBox.getSelectedItem();
            String email = emailField.getText().trim();

            if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(dialog,
                        "Phone number must start with 09 or +63 and be 11 digits total!\n" +
                                "Examples: 09123456789 or +639123456789",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (User u : users) {
                if (u.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            User newUser;
            if ("RESIDENT".equals(role)) {
                try {
                    int residentID = Integer.parseInt(residentIdField.getText().trim());
                    newUser = new ResidentUser(username, password, email, phone, residentID);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid Resident ID!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                newUser = new Staff(username, password, email, phone);
            }

            users.add(newUser);
            SecureFileHandler.saveUsers(users);
            SecureFileHandler.logActivity(username, "ACCOUNT_CREATED");
            JOptionPane.showMessageDialog(dialog, "Account created successfully!\nYou can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        StyledButton cancelButton = new StyledButton("Cancel",
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        phone = phone.trim().replaceAll("[\\s-]", "");
        return phone.matches("^09\\d{9}$") || phone.matches("^\\+63\\d{10}$");
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
