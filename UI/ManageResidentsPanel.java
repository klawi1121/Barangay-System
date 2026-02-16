package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.border.*;

import data_model.*;
import user_management.User;

public class ManageResidentsPanel extends JPanel {
private User panelUser;
        private List<Resident> panelResidents;
        private List<User> panelUsers;
        private StyledTable residentsTable;
        private DefaultTableModel tableModel;
        private StyledTextField searchField;
        private Stack<Resident> undoStack;
        private TableRowSorter<DefaultTableModel> sorter;
        
        private JPanel bottomButtonPanel;
        private StyledButton undoButton;
        
        public ManageResidentsPanel(User user, List<Resident> residents, List<User> users) {
            this.panelUser = user;
            this.panelResidents = residents;
            this.panelUsers = users;
            this.undoStack = new Stack<>();
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            
            // Top Toolbar Panel
            JPanel toolbar = new JPanel(new BorderLayout());
            toolbar.setBackground(Color.WHITE);
            toolbar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Left toolbar - Main action buttons
            JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftToolbar.setBackground(Color.WHITE);
            
            StyledButton addButton = new StyledButton("Add New Resident", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            addButton.addActionListener(e -> showAddResidentDialog());
            
            if (panelUser.canAccessAdminPanel()) {
                StyledButton editButton = new StyledButton("Edit Selected", 
                    BarangayColors.BUTTON_BLACK, Color.WHITE,
                    BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
                editButton.addActionListener(e -> editSelectedResident());
                
                StyledButton deleteButton = new StyledButton("Delete Selected", 
                    BarangayColors.BUTTON_BLACK, Color.WHITE,
                    BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
                deleteButton.addActionListener(e -> deleteSelectedResident());
                
                StyledButton deceasedButton = new StyledButton("Mark as Deceased", 
                    BarangayColors.BUTTON_BLACK, Color.WHITE,
                    BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
                deceasedButton.addActionListener(e -> markAsDeceased());
                
                leftToolbar.add(addButton);
                leftToolbar.add(editButton);
                leftToolbar.add(deleteButton);
                leftToolbar.add(deceasedButton);
            } else {
                // Residents can only view, not add/edit/delete
                leftToolbar.add(addButton);
                addButton.setEnabled(false);
                addButton.setToolTipText("Only administrators and staff can add residents");
            }
            
            // Right toolbar - Search only (removed Sort button)
            JPanel rightToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            rightToolbar.setBackground(Color.WHITE);
            
            JLabel searchLabel = new JLabel("Search:");
            searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            searchLabel.setForeground(BarangayColors.TEXT_COLOR);
            
            searchField = new StyledTextField(20);
            searchField.putClientProperty("JTextField.placeholderText", "Search by name, address, or contact...");
            
            StyledButton searchButton = new StyledButton("Search", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            searchButton.addActionListener(e -> searchResidents());
            
            StyledButton clearButton = new StyledButton("Clear", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            clearButton.addActionListener(e -> {
                searchField.setText("");
                loadResidentsData();
            });
            
            rightToolbar.add(searchLabel);
            rightToolbar.add(searchField);
            rightToolbar.add(searchButton);
            rightToolbar.add(clearButton);
            
            toolbar.add(leftToolbar, BorderLayout.WEST);
            toolbar.add(rightToolbar, BorderLayout.EAST);
            
            // Table Panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(Color.WHITE);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
            
            // CHANGED: Removed Household ID column
            String[] columns = {"ID", "Full Name", "Age", "Sex", "Address", "Contact", "Position", "Status", "Household Size"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
                
                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 0 || column == 2 || column == 8) return Integer.class;
                    return String.class;
                }
            };
            
            residentsTable = new StyledTable(tableModel);
            sorter = new TableRowSorter<>(tableModel);
            residentsTable.setRowSorter(sorter);
            
            residentsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            residentsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            residentsTable.getColumnModel().getColumn(2).setPreferredWidth(50);
            residentsTable.getColumnModel().getColumn(3).setPreferredWidth(70);
            residentsTable.getColumnModel().getColumn(4).setPreferredWidth(250);
            residentsTable.getColumnModel().getColumn(5).setPreferredWidth(120);
            residentsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
            residentsTable.getColumnModel().getColumn(7).setPreferredWidth(100);
            residentsTable.getColumnModel().getColumn(8).setPreferredWidth(100);
            
            residentsTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        viewResidentDetails();
                    }
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(residentsTable);
            scrollPane.setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
            scrollPane.getViewport().setBackground(Color.WHITE);
            
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            
            // Bottom Panel with Status and Undo Button
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBackground(Color.WHITE);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
            
            JLabel countLabel = new JLabel();
            countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            countLabel.setForeground(BarangayColors.TEXT_COLOR);
            
            bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            bottomButtonPanel.setBackground(Color.WHITE);
            
            undoButton = new StyledButton("Undo", 
                BarangayColors.ACCENT_ORANGE, Color.WHITE,
                BarangayColors.ACCENT_ORANGE.brighter(), BarangayColors.ACCENT_ORANGE.darker());
            undoButton.setPreferredSize(new Dimension(100, 35));
            undoButton.addActionListener(e -> undoLastAction());
            
            if (panelUser.canAccessAdminPanel()) {
                bottomButtonPanel.add(undoButton);
            }
            
            bottomPanel.add(countLabel, BorderLayout.WEST);
            bottomPanel.add(bottomButtonPanel, BorderLayout.EAST);
            
            tablePanel.add(bottomPanel, BorderLayout.SOUTH);
            
            add(toolbar, BorderLayout.NORTH);
            add(tablePanel, BorderLayout.CENTER);
            
            loadResidentsData();
            
            SwingUtilities.invokeLater(() -> {
                countLabel.setText("Total Active Records: " + tableModel.getRowCount());
            });
        }
        
        private void loadResidentsData() {
            tableModel.setRowCount(0);
            
            // CHANGED: Always sort by ID automatically
            List<Resident> sortedResidents = new ArrayList<>(panelResidents);
            sortedResidents.sort(Comparator.comparingInt(Resident::getResidentID));
            
            for (Resident resident : sortedResidents) {
                // CHANGED: Resident users can only see their own record in Manage Residents
                if (panelUser.getRole().equals("RESIDENT")) {
                    ResidentUser ru = (ResidentUser) panelUser;
                    if (resident.getResidentID() != ru.getResidentID()) {
                        continue;
                    }
                }
                
                String status;
                if (resident.getStatus() == Resident.ResidentStatus.DECEASED) {
                    status = "Deceased";
                } else if (resident.getAge() >= 60) {
                    status = "Senior Citizen";
                } else if (resident.getAge() <= 12) {
                    status = "Child";
                } else {
                    status = "Adult";
                }
                
                // Format ID as 6-digit
                String formattedId = String.format("%06d", resident.getResidentID());
                
                tableModel.addRow(new Object[]{
                    formattedId,
                    resident.getFullName(),
                    resident.getAge(),
                    resident.getSex(),
                    resident.getAddress(),
                    resident.getContactNumber(),
                    resident.getPosition(),
                    status,
                    resident.getHouseholdSize()
                });
            }
        }
        
        private void showAddResidentDialog() {
            if (!panelUser.canAccessAdminPanel()) {
                JOptionPane.showMessageDialog(this, 
                    "Only administrators and staff can add residents!", 
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            AddEditResidentDialog dialog = new AddEditResidentDialog(frame, null, panelResidents);
            if (dialog.showDialog()) {
                Resident newResident = dialog.getResident();
                panelResidents.add(newResident);
                SecureFileHandler.incrementResidentId(); // Increment ID counter
                undoStack.push(copyResident(newResident));
                SecureFileHandler.saveResidents(panelResidents);
                SecureFileHandler.logActivity(panelUser.getUsername(), "RESIDENT_ADDED: " + 
                    String.format("%06d", newResident.getResidentID()));
                loadResidentsData();
                JOptionPane.showMessageDialog(this, 
                    "Resident added successfully!\nHousehold Members: " + newResident.getHouseholdMembers().size(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        private void editSelectedResident() {
            if (!panelUser.canAccessAdminPanel()) {
                JOptionPane.showMessageDialog(this, 
                    "Only administrators and staff can edit residents!", 
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int selectedRow = residentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a resident to edit!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = residentsTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            Resident resident = findResidentById(residentID);
            
            if (resident != null) {
                Resident oldResident = copyResident(resident);
                AddEditResidentDialog dialog = new AddEditResidentDialog(frame, resident, panelResidents);
                if (dialog.showDialog()) {
                    undoStack.push(oldResident);
                    SecureFileHandler.saveResidents(panelResidents);
                    SecureFileHandler.logActivity(panelUser.getUsername(), "RESIDENT_UPDATED: " + 
                        String.format("%06d", residentID));
                    loadResidentsData();
                    JOptionPane.showMessageDialog(this, 
                        "Resident updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        private void deleteSelectedResident() {
            if (!panelUser.canAccessAdminPanel()) {
                JOptionPane.showMessageDialog(this, 
                    "Only administrators can delete residents!", 
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int selectedRow = residentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a resident to delete!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = residentsTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            String residentName = (String) tableModel.getValueAt(modelRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "<html>Are you sure you want to delete:<br><b>" + residentName + "</b> (ID: " + 
                String.format("%06d", residentID) + ")?<br><br>" +
                "This will also remove all household members!</html>", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                Resident resident = findResidentById(residentID);
                if (resident != null) {
                    panelResidents.remove(resident);
                    undoStack.push(resident);
                    SecureFileHandler.saveResidents(panelResidents);
                    SecureFileHandler.logActivity(panelUser.getUsername(), "RESIDENT_DELETED: " + 
                        String.format("%06d", residentID));
                    loadResidentsData();
                    JOptionPane.showMessageDialog(this, 
                        "Resident deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        private void markAsDeceased() {
            if (!panelUser.canAccessAdminPanel()) {
                JOptionPane.showMessageDialog(this, 
                    "Only administrators and staff can mark residents as deceased!", 
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int selectedRow = residentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a resident!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = residentsTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            String residentName = (String) tableModel.getValueAt(modelRow, 1);
            Resident resident = findResidentById(residentID);
            
            if (resident != null) {
                if (resident.getStatus() == Resident.ResidentStatus.DECEASED) {
                    JOptionPane.showMessageDialog(this, 
                        "This resident is already marked as deceased!", 
                        "Already Deceased", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "<html>Are you sure you want to mark:<br><b>" + residentName + "</b> (ID: " + 
                    String.format("%06d", residentID) + ")<br>as DECEASED?<br><br>" +
                    "This will archive their record and all household members will be removed.</html>", 
                    "Confirm Deceased Status", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Resident oldResident = copyResident(resident);
                    resident.setStatus(Resident.ResidentStatus.DECEASED);
                    SecureFileHandler.archiveDeceased(resident);
                    panelResidents.remove(resident);
                    undoStack.push(oldResident);
                    SecureFileHandler.saveResidents(panelResidents);
                    SecureFileHandler.logActivity(panelUser.getUsername(), "RESIDENT_DECEASED: " + 
                        String.format("%06d", residentID));
                    loadResidentsData();
                    JOptionPane.showMessageDialog(this, 
                        "Resident marked as deceased and archived.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        private void undoLastAction() {
            if (undoStack.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No actions to undo.", 
                    "Undo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Resident lastAction = undoStack.pop();
            boolean exists = false;
            
            for (Resident r : panelResidents) {
                if (r.getResidentID() == lastAction.getResidentID()) {
                    exists = true;
                    copyResidentData(lastAction, r);
                    break;
                }
            }
            
            if (!exists) {
                panelResidents.add(lastAction);
            }
            
            SecureFileHandler.saveResidents(panelResidents);
            SecureFileHandler.logActivity(panelUser.getUsername(), "UNDO_ACTION");
            loadResidentsData();
            JOptionPane.showMessageDialog(this, 
                "Last action undone successfully.", 
                "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void searchResidents() {
            String query = searchField.getText().toLowerCase().trim();
            if (query.isEmpty()) {
                loadResidentsData();
                return;
            }
            
            tableModel.setRowCount(0);
            
            for (Resident resident : panelResidents) {
                if (panelUser.getRole().equals("RESIDENT")) {
                    ResidentUser ru = (ResidentUser) panelUser;
                    if (resident.getResidentID() != ru.getResidentID()) {
                        continue;
                    }
                }
                
                if (resident.getFullName().toLowerCase().contains(query) ||
                    resident.getAddress().toLowerCase().contains(query) ||
                    resident.getContactNumber().contains(query) ||
                    String.format("%06d", resident.getResidentID()).contains(query)) {
                    
                    String status = resident.getStatus() == Resident.ResidentStatus.DECEASED ? "Deceased" :
                                   resident.getAge() >= 60 ? "Senior Citizen" : 
                                   resident.getAge() <= 12 ? "Child" : "Adult";
                    
                    String formattedId = String.format("%06d", resident.getResidentID());
                    
                    tableModel.addRow(new Object[]{
                        formattedId,
                        resident.getFullName(),
                        resident.getAge(),
                        resident.getSex(),
                        resident.getAddress(),
                        resident.getContactNumber(),
                        resident.getPosition(),
                        status,
                        resident.getHouseholdSize()
                    });
                }
            }
        }
        
        private void viewResidentDetails() {
            int selectedRow = residentsTable.getSelectedRow();
            if (selectedRow == -1) return;
            
            int modelRow = residentsTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            Resident resident = findResidentById(residentID);
            
            if (resident != null) {
                JDialog detailsDialog = new JDialog(frame, "Resident Details", true);
                detailsDialog.setSize(600, 700);
                detailsDialog.setLocationRelativeTo(frame);
                detailsDialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);
                
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.setBackground(Color.WHITE);
                mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(Color.WHITE);
                
                JLabel titleLabel = new JLabel("Resident Information");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                
                JLabel idLabel = new JLabel("ID: " + String.format("%06d", resident.getResidentID()));
                idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                idLabel.setForeground(Color.GRAY);
                
                headerPanel.add(titleLabel, BorderLayout.WEST);
                headerPanel.add(idLabel, BorderLayout.EAST);
                
                JPanel detailsPanel = new JPanel(new GridBagLayout());
                detailsPanel.setBackground(Color.WHITE);
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                
                // Personal Information Section
                JLabel personalLabel = new JLabel("PERSONAL INFORMATION");
                personalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                personalLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                detailsPanel.add(personalLabel, gbc);
                gbc.gridy++;
                
                gbc.gridwidth = 1;
                addDetailRow(detailsPanel, gbc, "Full Name:", resident.getFullName());
                addDetailRow(detailsPanel, gbc, "Age:", String.valueOf(resident.getAge()));
                addDetailRow(detailsPanel, gbc, "Birthday:", resident.getBirthday());
                addDetailRow(detailsPanel, gbc, "Sex:", resident.getSex());
                addDetailRow(detailsPanel, gbc, "Position:", resident.getPosition());
                addDetailRow(detailsPanel, gbc, "Civil Status:", resident.getMaritalStatus());
                
                gbc.gridy++;
                gbc.gridwidth = 2;
                JLabel contactLabel = new JLabel("CONTACT INFORMATION");
                contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                contactLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                detailsPanel.add(contactLabel, gbc);
                gbc.gridy++;
                gbc.gridwidth = 1;
                
                addDetailRow(detailsPanel, gbc, "Address:", resident.getAddress());
                addDetailRow(detailsPanel, gbc, "Contact:", resident.getContactNumber());
                addDetailRow(detailsPanel, gbc, "Occupation:", resident.getOccupation());
                
                gbc.gridy++;
                gbc.gridwidth = 2;
                JLabel householdLabel = new JLabel("HOUSEHOLD INFORMATION");
                householdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                householdLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                detailsPanel.add(householdLabel, gbc);
                gbc.gridy++;
                gbc.gridwidth = 1;
                
                addDetailRow(detailsPanel, gbc, "Household Head:", resident.isHouseholdHead() ? "Yes" : 
                    "ID: " + String.format("%06d", resident.getHouseholdHeadID()));
                addDetailRow(detailsPanel, gbc, "Household Size:", String.valueOf(resident.getHouseholdSize()));
                
                // Household Members Section
                if (!resident.getHouseholdMembers().isEmpty()) {
                    gbc.gridy++;
                    gbc.gridwidth = 2;
                    JLabel membersLabel = new JLabel("HOUSEHOLD MEMBERS");
                    membersLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    membersLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                    detailsPanel.add(membersLabel, gbc);
                    gbc.gridy++;
                    gbc.gridwidth = 1;
                    
                    for (HouseholdMember member : resident.getHouseholdMembers()) {
                        addDetailRow(detailsPanel, gbc, "Member:", member.getFullName() + 
                            " - Age: " + member.getAge() + 
                            " - Civil Status: " + member.getCivilStatus());
                    }
                }
                
                gbc.gridy++;
                gbc.gridwidth = 2;
                JLabel systemLabel = new JLabel("SYSTEM INFORMATION");
                systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                systemLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                detailsPanel.add(systemLabel, gbc);
                gbc.gridy++;
                gbc.gridwidth = 1;
                
                addDetailRow(detailsPanel, gbc, "Status:", resident.getStatus().toString());
                addDetailRow(detailsPanel, gbc, "Created:", 
                    resident.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                addDetailRow(detailsPanel, gbc, "Last Updated:", 
                    resident.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                
                if (resident.getDeceasedAt() != null) {
                    addDetailRow(detailsPanel, gbc, "Deceased:", 
                        resident.getDeceasedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
                
                JScrollPane scrollPane = new JScrollPane(detailsPanel);
                scrollPane.setBorder(null);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                
                mainPanel.add(headerPanel, BorderLayout.NORTH);
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.setBackground(Color.WHITE);
                StyledButton closeButton = new StyledButton("Close", 
                    BarangayColors.BUTTON_BLACK, Color.WHITE,
                    BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
                closeButton.addActionListener(e -> detailsDialog.dispose());
                buttonPanel.add(closeButton);
                mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                detailsDialog.add(mainPanel);
                detailsDialog.setVisible(true);
            }
        }
        
        private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(BarangayColors.TEXT_COLOR);
            panel.add(lbl, gbc);
            
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            JTextField field = new JTextField(value);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setEditable(false);
            field.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            field.setBackground(BarangayColors.LIGHT_BACKGROUND);
            field.setPreferredSize(new Dimension(300, 30));
            panel.add(field, gbc);
            
            gbc.gridy++;
        }
        
        private Resident findResidentById(int id) {
            for (Resident resident : panelResidents) {
                if (resident.getResidentID() == id) {
                    return resident;
                }
            }
            return null;
        }
        
        private Resident copyResident(Resident original) {
            Resident copy;
            if (original.isHouseholdHead()) {
                copy = new Resident(
                    original.getResidentID(),
                    original.getFirstName(),
                    original.getMiddleName(),
                    original.getLastName(),
                    original.getSuffix(),
                    original.getAge(),
                    original.getDateofBirth(),
                    original.getSex(),
                    original.getMedicalCondition(),
                    original.getIncomeBracket(),
                    original.getMotherTongue(),
                    original.getReligion(),
                    original.getEmployment(),
                    original.getMaritalStatus(),
                    original.getAddress(),
                    original.getPosition(),
                    original.getContactNumber(),
                    original.getOccupation()
                );
            } else {
                copy = new Resident(
                    original.getResidentID(),
                    original.getFirstName(),
                    original.getMiddleName(),
                    original.getLastName(),
                    original.getSuffix(),
                    original.getAge(),
                    original.getDateofBirth(),
                    original.getSex(),
                    original.getMedicalCondition(),
                    original.getIncomeBracket(),
                    original.getMotherTongue(),
                    original.getReligion(),
                    original.getEmployment(),
                    original.getMaritalStatus(),
                    original.getAddress(),
                    original.getPosition(),
                    original.getContactNumber(),
                    original.getOccupation(),
                    original.getHouseholdHeadID()
                );
            }
            copy.setStatus(original.getStatus());
            copy.setHouseholdMembers(new ArrayList<>(original.getHouseholdMembers()));
            return copy;
        }
        
        private void copyResidentData(Resident source, Resident destination) {
            destination.setFirstName(source.getFirstName());
            destination.setMiddleName(source.getMiddleName());
            destination.setLastName(source.getLastName());
            destination.setSuffix(source.getSuffix());
            destination.setAge(source.getAge());
            destination.setDateofBirth(source.getDateofBirth());
            destination.setSex(source.getSex());
            destination.setMedicalCondition(source.getMedicalCondition());
            destination.setIncomeBracket(source.getIncomeBracket());
            destination.setMotherTongue(source.getMotherTongue());
            destination.setReligion(source.getReligion());
            destination.setEmployment(source.getEmployment());
            destination.setMaritalStatus(source.getMaritalStatus());
            destination.setAddress(source.getAddress());
            destination.setPosition(source.getPosition());
            destination.setContactNumber(source.getContactNumber());
            destination.setOccupation(source.getOccupation());
            destination.setHouseholdHeadID(source.getHouseholdHeadID());
            destination.setHouseholdHead(source.isHouseholdHead());
            destination.setStatus(source.getStatus());
            destination.setHouseholdMembers(new ArrayList<>(source.getHouseholdMembers()));
        }
    }