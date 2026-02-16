package UI;

public class AddEditResidentDialog {
    private JDialog dialog;
        private Resident resident;
        private boolean saved;
        private List<Resident> dialogResidents;
        
        private StyledTextField idField, firstNameField, mInitialField, lastNameField, qualifierField,
                               ageField, birthdayField, medicalConditionField,
                               motherTongueField, religionField, maritalStatusField, 
                               addressField, positionField, contactField, 
                               occupationField;
        private StyledComboBox<String> sexComboBox, incomeComboBox, employmentComboBox;
        
        // Household Members
        private DefaultTableModel memberTableModel;
        private JTable memberTable;
        private List<HouseholdMember> householdMembers;
        
        public AddEditResidentDialog(JFrame parent, Resident existingResident, List<Resident> residents) {
            this.resident = existingResident;
            this.saved = false;
            this.dialogResidents = residents;
            this.householdMembers = existingResident != null ? 
                new ArrayList<>(existingResident.getHouseholdMembers()) : new ArrayList<>();
            
            dialog = new JDialog(parent, existingResident == null ? "Add New Resident (Household Head)" : "Edit Resident", true);
            dialog.setSize(700, 900);
            dialog.setLocationRelativeTo(parent);
            dialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel titleLabel = new JLabel(existingResident == null ? 
                "Add New Resident - Household Head" : "Edit Resident");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            int row = 0;
            
            // Resident ID - Auto-generated, read-only
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Resident ID*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            idField = new StyledTextField(15);
            idField.putClientProperty("JTextField.placeholderText", "Auto-generated");
            if (existingResident != null) {
                idField.setText(String.format("%06d", existingResident.getResidentID()));
                idField.setEditable(false);
                idField.setBackground(BarangayColors.LIGHT_BACKGROUND);
            } else {
                // Get next ID from counter
                String nextId = SecureFileHandler.getNextResidentIdFormatted();
                idField.setText(nextId);
                idField.setEditable(false);
                idField.setBackground(BarangayColors.LIGHT_BACKGROUND);
            }
            formPanel.add(idField, gbc);
            row++;
            
            // First Name
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("First Name*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            firstNameField = new StyledTextField(15);
            firstNameField.putClientProperty("JTextField.placeholderText", "Enter first name");
            if (existingResident != null) firstNameField.setText(existingResident.getFirstName());
            formPanel.add(firstNameField, gbc);
            row++;
            
            // Middle Initial
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Middle Initial:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            mInitialField = new StyledTextField(5);
            mInitialField.putClientProperty("JTextField.placeholderText", "M.I.");
            if (existingResident != null) mInitialField.setText(existingResident.getMInitial());
            formPanel.add(mInitialField, gbc);
            row++;
            
            // Last Name
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Last Name*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            lastNameField = new StyledTextField(15);
            lastNameField.putClientProperty("JTextField.placeholderText", "Enter last name");
            if (existingResident != null) lastNameField.setText(existingResident.getLastName());
            formPanel.add(lastNameField, gbc);
            row++;
            
            // Qualifier (Jr., Sr., III, etc.)
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Qualifier:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            qualifierField = new StyledTextField(10);
            qualifierField.putClientProperty("JTextField.placeholderText", "Jr., Sr., III, etc.");
            if (existingResident != null) qualifierField.setText(existingResident.getQualifier());
            formPanel.add(qualifierField, gbc);
            row++;
            
            // Age
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Age*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            ageField = new StyledTextField(5);
            ageField.putClientProperty("JTextField.placeholderText", "e.g., 25");
            if (existingResident != null) ageField.setText(String.valueOf(existingResident.getAge()));
            formPanel.add(ageField, gbc);
            row++;
            
            // Birthday
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Birthday*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            birthdayField = new StyledTextField(15);
            birthdayField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
            if (existingResident != null) birthdayField.setText(existingResident.getBirthday());
            formPanel.add(birthdayField, gbc);
            row++;
            
            // Sex
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Sex*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            sexComboBox = new StyledComboBox<>(new String[]{"Male", "Female"});
            if (existingResident != null) sexComboBox.setSelectedItem(existingResident.getSex());
            formPanel.add(sexComboBox, gbc);
            row++;
            
            // Position in Household
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Position*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            positionField = new StyledTextField(15);
            positionField.putClientProperty("JTextField.placeholderText", "Household Head, Spouse, Child, etc.");
            if (existingResident != null) {
                positionField.setText(existingResident.getPosition());
            } else {
                positionField.setText("Household Head");
            }
            formPanel.add(positionField, gbc);
            row++;
            
            // Civil Status
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Civil Status*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            maritalStatusField = new StyledTextField(15);
            maritalStatusField.putClientProperty("JTextField.placeholderText", "Single, Married, Widowed, etc.");
            if (existingResident != null) maritalStatusField.setText(existingResident.getMaritalStatus());
            formPanel.add(maritalStatusField, gbc);
            row++;
            
            // Medical Condition
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Medical Condition:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            medicalConditionField = new StyledTextField(20);
            medicalConditionField.putClientProperty("JTextField.placeholderText", "e.g., eczema, lung cancer");
            if (existingResident != null) medicalConditionField.setText(existingResident.getMedicalCondition());
            formPanel.add(medicalConditionField, gbc);
            row++;
            
            // Income Bracket
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Income Bracket:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            String[] incomeOptions = {"Select income level", "Less than ₱20,000", "₱20,000 - ₱40,000", 
                                     "₱40,001 - ₱60,000", "₱60,001 - ₱80,000", "₱80,001 - ₱100,000", 
                                     "More than ₱100,000"};
            incomeComboBox = new StyledComboBox<>(incomeOptions);
            if (existingResident != null) {
                int income = existingResident.getIncomeBracket();
                if (income < 20000) incomeComboBox.setSelectedItem("Less than ₱20,000");
                else if (income <= 40000) incomeComboBox.setSelectedItem("₱20,000 - ₱40,000");
                else if (income <= 60000) incomeComboBox.setSelectedItem("₱40,001 - ₱60,000");
                else if (income <= 80000) incomeComboBox.setSelectedItem("₱60,001 - ₱80,000");
                else if (income <= 100000) incomeComboBox.setSelectedItem("₱80,001 - ₱100,000");
                else if (income > 100000) incomeComboBox.setSelectedItem("More than ₱100,000");
                else incomeComboBox.setSelectedIndex(0);
            }
            formPanel.add(incomeComboBox, gbc);
            row++;
            
            // Mother Tongue
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Mother Tongue:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            motherTongueField = new StyledTextField(15);
            motherTongueField.putClientProperty("JTextField.placeholderText", "e.g., Tagalog, Cebuano");
            if (existingResident != null) motherTongueField.setText(existingResident.getMotherTongue());
            formPanel.add(motherTongueField, gbc);
            row++;
            
            // Religion
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Religion:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            religionField = new StyledTextField(15);
            religionField.putClientProperty("JTextField.placeholderText", "e.g., Catholic, Muslim");
            if (existingResident != null) religionField.setText(existingResident.getReligion());
            formPanel.add(religionField, gbc);
            row++;
            
            // Employment Dropdown
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Employment*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            String[] employmentOptions = {"Select employment status", "Employed", "Self-employed", 
                                         "Unemployed", "Student", "Retired", "OFW", "Contractual", 
                                         "Part-time", "Homemaker", "Disabled"};
            employmentComboBox = new StyledComboBox<>(employmentOptions);
            if (existingResident != null) {
                employmentComboBox.setSelectedItem(existingResident.getEmployment());
            }
            formPanel.add(employmentComboBox, gbc);
            row++;
            
            // Occupation
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Occupation:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            occupationField = new StyledTextField(15);
            occupationField.putClientProperty("JTextField.placeholderText", "e.g., Teacher, Farmer, Business Owner");
            if (existingResident != null) occupationField.setText(existingResident.getOccupation());
            formPanel.add(occupationField, gbc);
            row++;
            
            // Address
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Address*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            addressField = new StyledTextField(20);
            addressField.putClientProperty("JTextField.placeholderText", "Street, Barangay, City");
            if (existingResident != null) addressField.setText(existingResident.getAddress());
            formPanel.add(addressField, gbc);
            row++;
            
            // Contact Number
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Contact Number*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            contactField = new StyledTextField(15);
            contactField.putClientProperty("JTextField.placeholderText", "09xxxxxxxxx or +63xxxxxxxxx");
            contactField.setDocument(new PhoneDocument());
            if (existingResident != null) contactField.setText(existingResident.getContactNumber());
            formPanel.add(contactField, gbc);
            row++;
            
            // ============ HOUSEHOLD MEMBERS SECTION ============
            if (existingResident == null || existingResident.isHouseholdHead()) {
                gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                JLabel membersLabel = new JLabel("HOUSEHOLD MEMBERS");
                membersLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                membersLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                formPanel.add(membersLabel, gbc);
                row++;
                
                // Member Table
                gbc.gridy = row; gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0; gbc.weighty = 0.5;
                
                String[] memberColumns = {"Last Name", "First Name", "Qualifier", "Age", "Birthday", "Civil Status"};
                memberTableModel = new DefaultTableModel(memberColumns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
                memberTable = new JTable(memberTableModel);
                memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                memberTable.setRowHeight(25);
                JScrollPane memberScrollPane = new JScrollPane(memberTable);
                memberScrollPane.setPreferredSize(new Dimension(600, 150));
                formPanel.add(memberScrollPane, gbc);
                row++;
                
                // Load existing members
                for (HouseholdMember member : householdMembers) {
                    memberTableModel.addRow(new Object[]{
                        member.getLastName(),
                        member.getFirstName(),
                        member.getQualifier(),
                        member.getAge(),
                        member.getBirthday(),
                        member.getCivilStatus()
                    });
                }
                
                // Buttons for managing members
                gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 0; gbc.weighty = 0;
                gbc.gridwidth = 2;
                
                JPanel memberButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                memberButtonPanel.setBackground(Color.WHITE);
                
                StyledButton addMemberButton = new StyledButton("Add Household Member", 
                    BarangayColors.BUTTON_BLACK, Color.WHITE);
                addMemberButton.addActionListener(e -> showAddMemberDialog());
                
                StyledButton removeMemberButton = new StyledButton("Remove Selected Member", 
                    BarangayColors.BUTTON_BLACK, Color.WHITE);
                removeMemberButton.addActionListener(e -> removeSelectedMember());
                
                memberButtonPanel.add(addMemberButton);
                memberButtonPanel.add(removeMemberButton);
                formPanel.add(memberButtonPanel, gbc);
                row++;
            }
            
            // Button Panel
            gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(20, 5, 5, 5);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton saveButton = new StyledButton("Save Resident", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            saveButton.setPreferredSize(new Dimension(150, 35));
            saveButton.addActionListener(e -> saveResident());
            
            StyledButton cancelButton = new StyledButton("Cancel", 
                BarangayColors.BUTTON_BLACK, Color.WHITE,
                BarangayColors.BUTTON_HOVER, BarangayColors.BUTTON_ACTIVE);
            cancelButton.setPreferredSize(new Dimension(100, 35));
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            formPanel.add(buttonPanel, gbc);
            
            JScrollPane mainScrollPane = new JScrollPane(formPanel);
            mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            mainScrollPane.setBorder(null);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(mainScrollPane, BorderLayout.CENTER);
            
            dialog.add(mainPanel);
            
            dialog.getRootPane().registerKeyboardAction(
                e -> saveResident(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
            );
        }
        
        private void showAddMemberDialog() {
            JDialog memberDialog = new JDialog(dialog, "Add Household Member", true);
            memberDialog.setSize(500, 400);
            memberDialog.setLocationRelativeTo(dialog);
            memberDialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            int row = 0;
            
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            panel.add(new JLabel("Last Name*:"), gbc);
            gbc.gridx = 1;
            StyledTextField lastNameField = new StyledTextField(15);
            lastNameField.putClientProperty("JTextField.placeholderText", "Enter last name");
            panel.add(lastNameField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("First Name*:"), gbc);
            gbc.gridx = 1;
            StyledTextField firstNameField = new StyledTextField(15);
            firstNameField.putClientProperty("JTextField.placeholderText", "Enter first name");
            panel.add(firstNameField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Qualifier:"), gbc);
            gbc.gridx = 1;
            StyledTextField qualifierField = new StyledTextField(10);
            qualifierField.putClientProperty("JTextField.placeholderText", "Jr., Sr., III");
            panel.add(qualifierField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Age*:"), gbc);
            gbc.gridx = 1;
            StyledTextField ageField = new StyledTextField(5);
            ageField.putClientProperty("JTextField.placeholderText", "e.g., 25");
            panel.add(ageField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Birthday*:"), gbc);
            gbc.gridx = 1;
            StyledTextField birthdayField = new StyledTextField(15);
            birthdayField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
            panel.add(birthdayField, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Civil Status*:"), gbc);
            gbc.gridx = 1;
            StyledTextField civilStatusField = new StyledTextField(15);
            civilStatusField.putClientProperty("JTextField.placeholderText", "Single, Married, etc.");
            panel.add(civilStatusField, gbc);
            row++;
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton addButton = new StyledButton("Add Member", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            addButton.addActionListener(e -> {
                String lastName = lastNameField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String qualifier = qualifierField.getText().trim();
                String ageStr = ageField.getText().trim();
                String birthday = birthdayField.getText().trim();
                String civilStatus = civilStatusField.getText().trim();
                
                if (lastName.isEmpty() || firstName.isEmpty() || ageStr.isEmpty() || 
                    birthday.isEmpty() || civilStatus.isEmpty()) {
                    JOptionPane.showMessageDialog(memberDialog, 
                        "Please fill in all required fields!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    int age = Integer.parseInt(ageStr);
                    HouseholdMember member = new HouseholdMember(lastName, firstName, qualifier, 
                        age, birthday, civilStatus);
                    householdMembers.add(member);
                    memberTableModel.addRow(new Object[]{
                        lastName, firstName, qualifier, age, birthday, civilStatus
                    });
                    memberDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(memberDialog, 
                        "Please enter a valid age!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            StyledButton cancelButton = new StyledButton("Cancel", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            cancelButton.addActionListener(e -> memberDialog.dispose());
            
            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);
            
            memberDialog.add(panel);
            memberDialog.setVisible(true);
        }
        
        private void removeSelectedMember() {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please select a household member to remove!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            householdMembers.remove(selectedRow);
            memberTableModel.removeRow(selectedRow);
        }
        
        public boolean showDialog() {
            dialog.setVisible(true);
            return saved;
        }
        
        public Resident getResident() {
            return resident;
        }
        
        private void saveResident() {
            try {
                if (firstNameField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    ageField.getText().trim().isEmpty() ||
                    addressField.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty() ||
                    positionField.getText().trim().isEmpty() ||
                    maritalStatusField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(dialog, 
                        "Please fill in all required fields (marked with *)!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int id = Integer.parseInt(idField.getText().trim());
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String qualifier = qualifierField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String sex = (String) sexComboBox.getSelectedItem();
                String address = addressField.getText().trim();
                String contact = contactField.getText().trim();
                String position = positionField.getText().trim();
                String maritalStatus = maritalStatusField.getText().trim();
                
                if (!isValidPhoneNumber(contact)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Contact number must start with 09 or +63 and be 11 digits total!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String employment = (String) employmentComboBox.getSelectedItem();
                if (employment == null || employment.equals("Select employment status")) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please select an employment status!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String incomeSelection = (String) incomeComboBox.getSelectedItem();
                int incomeBracket = 0;
                if (incomeSelection != null && !incomeSelection.equals("Select income level")) {
                    if (incomeSelection.equals("Less than ₱20,000")) incomeBracket = 15000;
                    else if (incomeSelection.equals("₱20,000 - ₱40,000")) incomeBracket = 30000;
                    else if (incomeSelection.equals("₱40,001 - ₱60,000")) incomeBracket = 50000;
                    else if (incomeSelection.equals("₱60,001 - ₱80,000")) incomeBracket = 70000;
                    else if (incomeSelection.equals("₱80,001 - ₱100,000")) incomeBracket = 90000;
                    else if (incomeSelection.equals("More than ₱100,000")) incomeBracket = 120000;
                }
                
                if (resident == null) {
                    // Create new resident (household head)
                    resident = new Resident(
                        id, firstName, mInitialField.getText().trim(), lastName, qualifier,
                        age, birthdayField.getText().trim(), sex, medicalConditionField.getText().trim(),
                        incomeBracket,
                        motherTongueField.getText().trim(),
                        religionField.getText().trim(),
                        employment,
                        maritalStatus,
                        address,
                        position,
                        contact,
                        occupationField.getText().trim()
                    );
                    
                    // Add household members
                    for (HouseholdMember member : householdMembers) {
                        resident.addHouseholdMember(member);
                    }
                    
                } else {
                    // Update existing resident
                    resident.setFirstName(firstName);
                    resident.setMInitial(mInitialField.getText().trim());
                    resident.setLastName(lastName);
                    resident.setQualifier(qualifier);
                    resident.setAge(age);
                    resident.setBirthday(birthdayField.getText().trim());
                    resident.setSex(sex);
                    resident.setMedicalCondition(medicalConditionField.getText().trim());
                    resident.setIncomeBracket(incomeBracket);
                    resident.setMotherTongue(motherTongueField.getText().trim());
                    resident.setReligion(religionField.getText().trim());
                    resident.setEmployment(employment);
                    resident.setMaritalStatus(maritalStatus);
                    resident.setAddress(address);
                    resident.setPosition(position);
                    resident.setContactNumber(contact);
                    resident.setOccupation(occupationField.getText().trim());
                    resident.setHouseholdMembers(new ArrayList<>(householdMembers));
                }
                
                saved = true;
                dialog.dispose();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for numeric fields!", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private Resident findResidentById(int id) {
            for (Resident r : dialogResidents) {
                if (r.getResidentID() == id) {
                    return r;
                }
            }
            return null;
        }
        
        private boolean isValidPhoneNumber(String phone) {
            if (phone == null || phone.trim().isEmpty()) return false;
            phone = phone.trim().replaceAll("[\\s-]", "");
            if (phone.matches("^09\\d{9}$")) return true;
            if (phone.matches("^\\+63\\d{10}$")) return true;
            return false;
        }
    }
