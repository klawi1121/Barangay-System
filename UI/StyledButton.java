package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StyledButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color activeColor;
    private Color textColor;
    
    public StyledButton(String text) {
        super(text);
        this.normalColor = BarangayColors.BUTTON_BLACK;
        this.hoverColor = BarangayColors.BUTTON_HOVER;
        this.activeColor = BarangayColors.BUTTON_ACTIVE;
        this.textColor = Color.WHITE;
        setupButton();
    }
    
    public StyledButton(String text, Color bgColor, Color fgColor) {
        super(text);
        this.normalColor = bgColor;
        this.hoverColor = bgColor.brighter();
        this.activeColor = bgColor.darker();
        this.textColor = fgColor;
        setupButton();
    }
    
    public StyledButton(String text, Color bgColor, Color fgColor, Color hoverColor, Color activeColor) {
        super(text);
        this.normalColor = bgColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.textColor = fgColor;
        setupButton();
    }
    
    private void setupButton() {
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new RoundedBorder(5));
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(normalColor);
        setForeground(textColor);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                    setBorder(new RoundedBorder(5, BarangayColors.ACCENT_ORANGE));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(normalColor);
                    setBorder(new RoundedBorder(5));
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(activeColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(activeColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
