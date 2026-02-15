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
        
        public MainDashboard(User user, List<Resident> residents, List<User> users, JPanel mainPanel, CardLayout cardLayout) {
            this.dashboardUser = user;
            this.dashboardResidents = residents;
            this.dashboardUsers = users;
            this.sidebarButtons = new LinkedHashMap<>();
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            
            // ==================== SIDEBAR ====================
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(51, 51, 51));
            sidebar.setPreferredSize(new Dimension(240, 0));
            
            JLabel title = new JLabel("BRIS", SwingConstants.CENTER);
            title.setForeground(Color.WHITE);
            title.setFont(new Font("SansSerif", Font.BOLD, 24));
            title.setBorder(new EmptyBorder(20, 10, 20, 10));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(title);
            
            sidebar.add(Box.createVerticalStrut(10));
            
            JButton manageResidentsBtn = createSidebarButton("Manage Residents");
            JButton reportsBtn = createSidebarButton("Reports");
            JButton userManagementBtn = createSidebarButton("User Management");
            JButton accountSettingsBtn = createSidebarButton("Account Settings");
            
            sidebar.add(manageResidentsBtn);
            sidebar.add(reportsBtn);
            
            if (user instanceof Admin) {
                sidebar.add(userManagementBtn);
            }
            
            sidebar.add(accountSettingsBtn);
            
            sidebar.add(Box.createVerticalGlue());
            
            JButton logoutBtn = createSidebarButton("Logout");
            sidebar.add(logoutBtn);
            
            add(sidebar, BorderLayout.WEST);
            
            // ==================== CONTENT AREA ====================
            contentLayout = new CardLayout();
            contentPanel = new JPanel(contentLayout);
            contentPanel.setBackground(Color.WHITE);
            
            JPanel manageResidentsPanel = new ManageResidentsPanel(user, residents);
            JPanel reportsPanel = new ReportsPanel(residents);
            JPanel userManagementPanel = new UserManagementPanel(users);
            JPanel accountSettingsPanel = new AccountSettingsPanel(user, users, mainPanel, cardLayout, this);
            
            contentPanel.add(manageResidentsPanel, "Manage Residents");
            contentPanel.add(reportsPanel, "Reports");
            contentPanel.add(userManagementPanel, "User Management");
            contentPanel.add(accountSettingsPanel, "Account Settings");
            
            add(contentPanel, BorderLayout.CENTER);
            
            // Default view
            setActiveSidebar("Manage Residents");
            contentLayout.show(contentPanel, "Manage Residents");
            
            // Button actions
            manageResidentsBtn.addActionListener(e -> {
                setActiveSidebar("Manage Residents");
                contentLayout.show(contentPanel, "Manage Residents");
            });
            
            reportsBtn.addActionListener(e -> {
                setActiveSidebar("Reports");
                contentLayout.show(contentPanel, "Reports");
            });
            
            userManagementBtn.addActionListener(e -> {
                setActiveSidebar("User Management");
                contentLayout.show(contentPanel, "User Management");
            });
            
            accountSettingsBtn.addActionListener(e -> {
                setActiveSidebar("Account Settings");
                contentLayout.show(contentPanel, "Account Settings");
            });
            
            logoutBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    cardLayout.show(mainPanel, "LOGIN");
                    mainPanel.remove(this);
                }
            });
        }
        
        private JButton createSidebarButton(String text) {
            JButton btn = new JButton(text);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(51, 51, 51));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorder(new EmptyBorder(10, 20, 10, 20));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!btn.getBackground().equals(new Color(0, 102, 204))) {
                        btn.setBackground(new Color(70, 70, 70));
                    }
                }
                
                public void mouseExited(MouseEvent e) {
                    if (!btn.getBackground().equals(new Color(0, 102, 204))) {
                        btn.setBackground(new Color(51, 51, 51));
                    }
                }
            });
            
            sidebarButtons.put(text, btn);
            return btn;
        }
        
        private void setActiveSidebar(String active) {
            for (Map.Entry<String, JButton> entry : sidebarButtons.entrySet()) {
                if (entry.getKey().equals(active)) {
                    entry.getValue().setBackground(new Color(0, 102, 204));
                } else {
                    entry.getValue().setBackground(new Color(51, 51, 51));
                }
            }
        }
        
        public void refreshResidents(List<Resident> updatedResidents) {
            this.dashboardResidents = updatedResidents;
            // Force refresh by recreating panels if needed (kept as-is per original behavior)
        }
    }
    
    // ==================== MANAGE RESIDENTS PANEL ====================
    // CHANGED: Always sorted by ID, removed Sort button, removed Remove Households button
