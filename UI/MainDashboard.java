package UI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import UI.*;
import data_model.*;
import data_storage_security.*;
import user_management.*;

public class MainDashboard extends JPanel {
    private User dashboardUser;
    private List<Resident> dashboardResidents;
    private List<User> dashboardUsers;
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private Map<String, JButton> sidebarButtons;

    public MainDashboard(User user, List<Resident> residents, List<User> users) {
        this.dashboardUser = user;
        this.dashboardResidents = residents;
        this.dashboardUsers = users;
        this.sidebarButtons = new HashMap<>();

        setLayout(new BorderLayout());
        setBackground(BarangayColors.LIGHT_BACKGROUND);

        JPanel headerPanel = createHeader();
        JPanel sidebar = createSidebar();

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Color.WHITE);

        initializeContentPanels();

        add(headerPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BarangayColors.HEADER_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        JLabel logoLabel = new JLabel(" ");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Barangay Records System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel barangayLabel = new JLabel("Local Government Unit");
        barangayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        barangayLabel.setForeground(new Color(220, 220, 220));

        titlePanel.add(titleLabel);
        titlePanel.add(barangayLabel);

        leftPanel.add(logoLabel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(titlePanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);

        JLabel userLabel = new JLabel(dashboardUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(dashboardUser.getRole());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(220, 220, 220));

        userInfoPanel.add(userLabel);
        userInfoPanel.add(roleLabel);

        StyledButton logoutButton = new StyledButton("Logout",
                BarangayColors.ACCENT_ORANGE, Color.WHITE,
                BarangayColors.ACCENT_ORANGE.brighter(), BarangayColors.ACCENT_ORANGE.darker());
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> logout());

        rightPanel.add(userInfoPanel);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(logoutButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBackground(BarangayColors.SIDEBAR_GRAY);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BarangayColors.BORDER_COLOR));

        sidebar.add(Box.createVerticalStrut(20));

        createMenuButton(sidebar, "Manage Residents", "manage_residents");
        createMenuButton(sidebar, "Reports", "reports");

        if (dashboardUser.canManageUsers()) {
            createMenuButton(sidebar, "User Management", "user_management");
        }

        createMenuButton(sidebar, "Account Settings", "account_settings");

        sidebar.add(Box.createVerticalGlue());

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(210, 210, 210));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerPanel.setLayout(new BorderLayout());

        JLabel versionLabel = new JLabel("v2.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(BarangayColors.TEXT_COLOR);

        footerPanel.add(versionLabel, BorderLayout.SOUTH);

        sidebar.add(footerPanel);

        return sidebar;
    }

    private void createMenuButton(JPanel sidebar, String text, String panelName) {
        JButton menuButton = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isSelected() || getModel().isPressed()) {
                    g2.setColor(BarangayColors.PRIMARY_BLUE);
                    setForeground(Color.WHITE);
                } else if (getModel().isRollover()) {
                    g2.setColor(BarangayColors.PRIMARY_BLUE.brighter());
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(Color.WHITE);
                    setForeground(BarangayColors.TEXT_COLOR);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuButton.setMaximumSize(new Dimension(200, 45));
        menuButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuButton.setForeground(BarangayColors.TEXT_COLOR);
        menuButton.setBackground(Color.WHITE);
        menuButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.setHorizontalAlignment(SwingConstants.LEFT);
        menuButton.setContentAreaFilled(false);
        menuButton.setOpaque(false);

        menuButton.addActionListener(e -> {
            for (JButton btn : sidebarButtons.values()) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(BarangayColors.TEXT_COLOR);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BarangayColors.BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)));
            }

            menuButton.setBackground(BarangayColors.PRIMARY_BLUE);
            menuButton.setForeground(Color.WHITE);
            menuButton.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BarangayColors.PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));

            contentLayout.show(contentPanel, panelName);
        });

        sidebarButtons.put(panelName, menuButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(menuButton);
    }

    private void initializeContentPanels() {
        contentPanel.add(new ManageResidentsPanel(dashboardUser, dashboardResidents, dashboardUsers),
                "manage_residents");
        contentPanel.add(new ReportsPanel(dashboardUser, dashboardResidents), "reports");

        if (dashboardUser.canManageUsers()) {
            contentPanel.add(new UserManagementPanel(dashboardUser, dashboardUsers), "user_management");
        }

        contentPanel.add(new AccountSettingsPanel(dashboardUser), "account_settings");

        JButton firstButton = sidebarButtons.values().iterator().next();
        if (firstButton != null) {
            firstButton.doClick();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SecureFileHandler.logActivity(dashboardUser.getUsername(), "LOGOUT");
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null)
                w.dispose();
        }
    }
}
