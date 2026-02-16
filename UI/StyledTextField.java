package UI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

public class StyledTextField extends JTextField {
    public StyledTextField(int columns) {
        super(columns);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BarangayColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        setBackground(Color.WHITE);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (getText().isEmpty() && !isFocusOwner()) {
            String placeholder = (String) getClientProperty("JTextField.placeholderText");
            if (placeholder != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int x = insets.left;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, x, y);
                g2.dispose();
            }
        }
    }
}
