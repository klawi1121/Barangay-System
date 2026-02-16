package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StyledTable extends JTable {
    public StyledTable(DefaultTableModel model) {
        super(model);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setRowHeight(35);
        setShowGrid(true);
        setGridColor(new Color(230, 230, 230));
        setSelectionBackground(BarangayColors.PRIMARY_BLUE.brighter());
        setSelectionForeground(Color.WHITE);
        
        getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        getTableHeader().setBackground(BarangayColors.TABLE_HEADER);
        getTableHeader().setForeground(BarangayColors.TEXT_COLOR);
        getTableHeader().setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
        getTableHeader().setReorderingAllowed(false);
        
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    int modelRow = convertRowIndexToModel(row);
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    if (modelRow >= 0 && modelRow < model.getRowCount()) {
                        int statusColumn = model.findColumn("Status");
                        if (statusColumn >= 0) {
                            Object statusObj = model.getValueAt(modelRow, statusColumn);
                            if (statusObj != null && statusObj.toString().equals("Deceased")) {
                                c.setBackground(BarangayColors.ARCHIVE_COLOR);
                            } else if (row % 2 == 0) {
                                c.setBackground(Color.WHITE);
                            } else {
                                c.setBackground(new Color(248, 248, 248));
                            }
                        }
                    } else if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 248, 248));
                    }
                }
                
                ((JComponent)c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }
}