package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import data_model.*;
import user_management.User;

public class ManageResidentsPanel extends JPanel {

    private JTable residentTable;
    private DefaultTableModel tableModel;
    private List<Resident> residents;

    public ManageResidentsPanel(User user, List<Resident> residents) {
        this.residents = residents;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Manage Residents");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
                "Resident ID",
                "Last Name",
                "First Name",
                "Middle Name",
                "Suffix",
                "Gender",
                "Birthdate",
                "Age",
                "Civil Status",
                "Address",
                "Contact",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0);
        residentTable = new JTable(tableModel);
        residentTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(residentTable);
        add(scrollPane, BorderLayout.CENTER);

        loadResidentsToTable();
    }

    private void loadResidentsToTable() {
        tableModel.setRowCount(0);

        for (Resident r : residents) {
            tableModel.addRow(new Object[] {
                    r.getResidentID(),
                    r.getLastName(),
                    r.getFirstName(),
                    r.getMiddleName(),
                    r.getSuffix(),
                    r.getGender(),
                    r.getDateOfBirth(),
                    r.getAge(),
                    r.getCivilStatus(),
                    r.getAddress(),
                    r.getContactNumber(),
                    r.getStatus()
            });
        }
    }
}
