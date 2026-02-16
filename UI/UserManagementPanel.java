package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import data_storage_security.SecureFileHandler;
import user_management.User;


public class UserManagementPanel extends JPanel {        
        private User panelUser;
        private List<User> panelUsers;
        private StyledTable usersTable;
        private DefaultTableModel tableModel;
        
        public UserManagementPanel(User user, List<User> users) {
            this.panelUser = user;
            this.panelUsers = users;
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JLabel titleLabel = new JLabel("User Management");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JLabel subtitleLabel = new JLabel("Manage system users and permissions");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitleLabel.setForeground(Color.GRAY);
            
            JPanel titlePanel = new JPanel(new GridLayout(2, 1));
            titlePanel.setBackground(Color.WHITE);
            titlePanel.add(titleLabel);
            titlePanel.add(subtitleLabel);
            
            JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            toolbar.setBackground(Color.WHITE);
            
            StyledButton removeButton = new StyledButton("Remove User", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            removeButton.addActionListener(e -> removeSelectedUser());
            
            StyledButton toggleButton = new StyledButton("Toggle Status", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            toggleButton.addActionListener(e -> toggleUserStatus());
            
            toolbar.add(removeButton);
            toolbar.add(toggleButton);
            
            headerPanel.add(titlePanel, BorderLayout.WEST);
            headerPanel.add(toolbar, BorderLayout.EAST);
            
            String[] columns = {"Username", "Role", "Email", "Phone", "Status", "Last Login"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            usersTable = new StyledTable(tableModel);
            usersTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            usersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            usersTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            usersTable.getColumnModel().getColumn(3).setPreferredWidth(120);
            usersTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            usersTable.getColumnModel().getColumn(5).setPreferredWidth(150);
            
            JScrollPane scrollPane = new JScrollPane(usersTable);
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(0, 15, 15, 15)
            ));
            scrollPane.getViewport().setBackground(Color.WHITE);
            
            add(headerPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            
            loadUsersData();
        }
        
        private void loadUsersData() {
            tableModel.setRowCount(0);
            for (User user : panelUsers) {
                List<String> logs = SecureFileHandler.getLoginHistory(user.getUsername());
                String lastLogin = "Never";
                if (!logs.isEmpty()) {
                    String lastLog = logs.get(logs.size() - 1);
                    String[] parts = lastLog.split("\\|");
                    if (parts.length >= 1) {
                        lastLogin = parts[0];
                    }
                }
                
                String status = user.isActive() ? 
                    "<html><font color='green'>Active</font></html>" : 
                    "<html><font color='red'>Inactive</font></html>";
                
                tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhone(),
                    status,
                    lastLogin
                });
            }
        }
        
        private void removeSelectedUser() {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a user to remove!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = usersTable.convertRowIndexToModel(selectedRow);
            String username = (String) tableModel.getValueAt(modelRow, 0);
            
            if (username.equals(panelUser.getUsername())) {
                JOptionPane.showMessageDialog(this, 
                    "You cannot remove your own account!", 
                    "Operation Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (username.equals("admin")) {
                JOptionPane.showMessageDialog(this, 
                    "The default admin account cannot be removed!", 
                    "Operation Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Are you sure you want to remove user:<br><b>" + username + "</b>?</html>",
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                panelUsers.removeIf(u -> u.getUsername().equals(username));
                SecureFileHandler.saveUsers(panelUsers);
                SecureFileHandler.logActivity(panelUser.getUsername(), "USER_REMOVED: " + username);
                loadUsersData();
                JOptionPane.showMessageDialog(this, 
                    "User removed successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        private void toggleUserStatus() {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a user!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = usersTable.convertRowIndexToModel(selectedRow);
            String username = (String) tableModel.getValueAt(modelRow, 0);
            
            if (username.equals(panelUser.getUsername())) {
                JOptionPane.showMessageDialog(this, 
                    "You cannot change your own account status!", 
                    "Operation Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            for (User user : panelUsers) {
                if (user.getUsername().equals(username)) {
                    String newStatus = user.isActive() ? "deactivate" : "activate";
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to " + newStatus + " user: " + username + "?",
                        "Confirm Status Change", 
                        JOptionPane.YES_NO_OPTION, 
                        user.isActive() ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        user.setActive(!user.isActive());
                        SecureFileHandler.saveUsers(panelUsers);
                        SecureFileHandler.logActivity(panelUser.getUsername(), 
                            "USER_STATUS_CHANGED: " + username + " to " + (user.isActive() ? "Active" : "Inactive"));
                        loadUsersData();
                        
                        String message = user.isActive() ? 
                            "User activated successfully!" : "User deactivated successfully!";
                        JOptionPane.showMessageDialog(this, message, 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                }
            }
        }
    }