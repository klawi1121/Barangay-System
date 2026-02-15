package UI;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StyledTable extends JTable {

    public StyledTable(DefaultTableModel model) {
        super(model);

        setRowHeight(25);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setSelectionBackground(BarangayColors.SECONDARY);
        setGridColor(new Color (220, 220, 220));
        setShowGrid(true);

        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(BarangayColors.PRIMARY);
        header.setForeground(Color.WHITE);
    }
}