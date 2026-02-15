package UI;

import javax.swing.*;
import java.awt.*;

public class StyledPasswordField extends JPasswordField{
    
    public StyledPasswordField(int columns) {
        super(columns);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(Color.BLACK);
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    }
}
