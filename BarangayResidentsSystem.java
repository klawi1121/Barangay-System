import javax.swing.*;
import java.awt.*;
import UI.DashboardPanel;
import UI.ResidentManagementPanel;
import UI.UserManagementPanel;
import UI.AccountSettingsPanel;

public class BarangayResidentsSystem extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public BarangayResidentsSystem() {
        setTitle("Barangay Residents Information System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        DashboardPanel dashboardPanel = new DashboardPanel();
        ResidentManagementPanel residentPanel = new ResidentManagementPanel();
        UserManagementPanel userPanel = new UserManagementPanel();
        AccountSettingsPanel accountPanel = new AccountSettingsPanel();

        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(residentPanel, "Residents");
        mainPanel.add(userPanel, "Users");
        mainPanel.add(accountPanel, "Account");

        add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BarangayResidentsSystem();
        });
    }
}
