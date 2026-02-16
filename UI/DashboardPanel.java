package UI;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {

        setLayout(new BorderLayout());
        setBackground(BarangayColors.LIGHT_BACKGROUND);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BarangayColors.LIGHT_BACKGROUND);
        centerPanel.setLayout(new GridLayout(2, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        centerPanel.add(createCard("Total Residents"));
        centerPanel.add(createCard("Active Residents"));
        centerPanel.add(createCard("Inactive Residents"));
        centerPanel.add(createCard("Total Users"));

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(String title) {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }
}
