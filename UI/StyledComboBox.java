package UI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

public class StyledComboBox<T> extends JComboBox<T> {
    public StyledComboBox(T[] items) {
        super(items);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBackground(Color.WHITE);
        setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return c;
            }
        });
    }
}

