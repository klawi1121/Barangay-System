package UI;

import javax.swing.*;
import java.awt.*;

public class StyledButton extends JButton{

    public StyledButton(String text, Color bgColor){
        super(text);
        setFocusPainted(false);
        setBackground(bgColor);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
    
}
