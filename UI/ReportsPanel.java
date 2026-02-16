package UI;

import UI.*;
import user_management.*;




public class ReportsPanel extends JPanel {
    private User reportUser;
        private List<Resident> reportResidents;
        
        public ReportsPanel(User user, List<Resident> residents) {
            this.reportUser = user;
            this.reportResidents = residents;
            
            setLayout(new BorderLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel titleLabel = new JLabel("Barangay Reports & Analytics");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            JLabel dateLabel = new JLabel("As of: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);
            headerPanel.add(dateLabel, BorderLayout.EAST);
            
            JPanel reportsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
            reportsGrid.setBackground(Color.WHITE);
            reportsGrid.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
            
            ReportCard totalResidentsCard = new ReportCard("Total Residents", 
                "View complete barangay demographic", Color.decode("#4CAF50"));
            totalResidentsCard.addActionListener(e -> showTotalResidentsReport());
            
            ReportCard sexSummaryCard = new ReportCard("Sex Distribution", 
                "Breakdown by male and female", Color.decode("#2196F3"));
            sexSummaryCard.addActionListener(e -> showSexSummary());
            
            ReportCard ageGroupCard = new ReportCard("Age Group Analysis", 
                "Distribution across age categories", Color.decode("#FF9800"));
            ageGroupCard.addActionListener(e -> showAgeGroupSummary());
            
            ReportCard householdCard = new ReportCard("Household Summary", 
                "Average household size and total households", Color.decode("#9C27B0"));
            householdCard.addActionListener(e -> showHouseholdSummary());
            
            reportsGrid.add(totalResidentsCard);
            reportsGrid.add(sexSummaryCard);
            reportsGrid.add(ageGroupCard);
            reportsGrid.add(householdCard);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(reportsGrid, BorderLayout.CENTER);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        class ReportCard extends JPanel {
            public ReportCard(String title, String description, Color accentColor) {
                setLayout(new BorderLayout());
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BarangayColors.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setBackground(BarangayColors.LIGHT_BACKGROUND);
                        setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(accentColor, 2),
                            BorderFactory.createEmptyBorder(20, 20, 20, 20)
                        ));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        setBackground(Color.WHITE);
                        setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(BarangayColors.BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(20, 20, 20, 20)
                        ));
                    }
                });
                
                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                titleLabel.setForeground(BarangayColors.TEXT_COLOR);
                
                JLabel descLabel = new JLabel(description);
                descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                descLabel.setForeground(Color.GRAY);
                
                JPanel textPanel = new JPanel(new GridLayout(2, 1));
                textPanel.setBackground(getBackground());
                textPanel.add(titleLabel);
                textPanel.add(descLabel);
                
                JLabel iconLabel = new JLabel(">");
                iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                iconLabel.setForeground(accentColor);
                
                add(textPanel, BorderLayout.CENTER);
                add(iconLabel, BorderLayout.EAST);
            }
            
            public void addActionListener(ActionListener listener) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                    }
                });
            }
        }
        
        private void showTotalResidentsReport() {
            int total = reportResidents.size();
            int households = countHouseholdHeads();
            double avgAge = calculateAverageAge(reportResidents);
            int totalPopulation = calculateTotalPopulation();
            
            String message = String.format(
                "<html><div style='width: 450px;'>" +
                "<h2 style='color: %s;'>Barangay Demographic Report</h2>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>" +
                "<tr><td style='padding: 8px;'><b>Total Registered Residents:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Total Households:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Total Population:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Average Age:</b></td><td style='padding: 8px;'>%.1f years</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Average Household Size:</b></td><td style='padding: 8px;'>%.1f persons</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Report Date:</b></td><td style='padding: 8px;'>%s</td></tr>" +
                "</table>" +
                "</div></html>",
                String.format("#%02x%02x%02x", 
                    BarangayColors.PRIMARY_BLUE.getRed(),
                    BarangayColors.PRIMARY_BLUE.getGreen(),
                    BarangayColors.PRIMARY_BLUE.getBlue()),
                total, households, totalPopulation, avgAge, 
                (double) totalPopulation / households,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Barangay Demographic Report", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void showSexSummary() {
            int male = 0, female = 0;
            for (Resident r : reportResidents) {
                String sex = r.getSex().toLowerCase();
                if (sex.contains("male")) male++;
                else if (sex.contains("female")) female++;
            }
            
            int total = male + female;
            double malePct = total > 0 ? (male * 100.0 / total) : 0;
            double femalePct = total > 0 ? (female * 100.0 / total) : 0;
            
            String message = String.format(
                "<html><div style='width: 450px;'>" +
                "<h2 style='color: %s;'>Barangay Sex Distribution</h2>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>" +
                "<tr style='background-color: #e3f2fd;'><td style='padding: 10px;'><b>Male:</b></td>" +
                "<td style='padding: 10px;'>%d residents</td>" +
                "<td style='padding: 10px;'><b>%.1f%%</b></td></tr>" +
                "<tr style='background-color: #fce4ec;'><td style='padding: 10px;'><b>Female:</b></td>" +
                "<td style='padding: 10px;'>%d residents</td>" +
                "<td style='padding: 10px;'><b>%.1f%%</b></td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 10px;'><b>Total:</b></td>" +
                "<td style='padding: 10px;' colspan='2'><b>%d residents</b></td></tr>" +
                "</table>" +
                "</div></html>",
                String.format("#%02x%02x%02x", 
                    BarangayColors.PRIMARY_BLUE.getRed(),
                    BarangayColors.PRIMARY_BLUE.getGreen(),
                    BarangayColors.PRIMARY_BLUE.getBlue()),
                male, malePct, female, femalePct, total
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Sex Distribution Report", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void showAgeGroupSummary() {
            Map<String, Integer> ageGroups = new LinkedHashMap<>();
            ageGroups.put("Children (0-12)", 0);
            ageGroups.put("Teens (13-19)", 0);
            ageGroups.put("Young Adults (20-35)", 0);
            ageGroups.put("Adults (36-59)", 0);
            ageGroups.put("Seniors (60+)", 0);
            
            for (Resident r : reportResidents) {
                int age = r.getAge();
                if (age <= 12) ageGroups.put("Children (0-12)", ageGroups.get("Children (0-12)") + 1);
                else if (age <= 19) ageGroups.put("Teens (13-19)", ageGroups.get("Teens (13-19)") + 1);
                else if (age <= 35) ageGroups.put("Young Adults (20-35)", ageGroups.get("Young Adults (20-35)") + 1);
                else if (age <= 59) ageGroups.put("Adults (36-59)", ageGroups.get("Adults (36-59)") + 1);
                else ageGroups.put("Seniors (60+)", ageGroups.get("Seniors (60+)") + 1);
            }
            
            int total = reportResidents.size();
            StringBuilder rows = new StringBuilder();
            String[] colors = {"#e8f5e9", "#f1f8e9", "#fff3e0", "#ffecb3", "#ffccbc"};
            int i = 0;
            
            for (Map.Entry<String, Integer> entry : ageGroups.entrySet()) {
                double percentage = total > 0 ? (entry.getValue() * 100.0 / total) : 0;
                rows.append(String.format(
                    "<tr style='background-color: %s;'><td style='padding: 10px;'><b>%s</b></td>" +
                    "<td style='padding: 10px;'>%d residents</td>" +
                    "<td style='padding: 10px;'><b>%.1f%%</b></td></tr>",
                    colors[i % colors.length], entry.getKey(), entry.getValue(), percentage
                ));
                i++;
            }
            
            String message = String.format(
                "<html><div style='width: 500px;'>" +
                "<h2 style='color: %s;'>Barangay Age Group Distribution</h2>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>%s" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 10px;'><b>Total Residents:</b></td>" +
                "<td style='padding: 10px;' colspan='2'><b>%d residents</b></td></tr>" +
                "</table>" +
                "</div></html>",
                String.format("#%02x%02x%02x", 
                    BarangayColors.PRIMARY_BLUE.getRed(),
                    BarangayColors.PRIMARY_BLUE.getGreen(),
                    BarangayColors.PRIMARY_BLUE.getBlue()),
                rows.toString(), total
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Age Group Analysis", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void showHouseholdSummary() {
            int households = countHouseholdHeads();
            int totalPopulation = calculateTotalPopulation();
            double avgHouseholdSize = households > 0 ? (double) totalPopulation / households : 0;
            
            // Calculate household size distribution
            Map<Integer, Integer> householdSizes = new TreeMap<>();
            for (Resident r : reportResidents) {
                if (r.isHouseholdHead()) {
                    int size = r.getHouseholdSize();
                    householdSizes.put(size, householdSizes.getOrDefault(size, 0) + 1);
                }
            }
            
            StringBuilder sizeRows = new StringBuilder();
            for (Map.Entry<Integer, Integer> entry : householdSizes.entrySet()) {
                sizeRows.append(String.format(
                    "<tr><td style='padding: 5px;'><b>%d persons</b></td><td style='padding: 5px;'>%d households</td></tr>",
                    entry.getKey(), entry.getValue()
                ));
            }
            
            String message = String.format(
                "<html><div style='width: 450px;'>" +
                "<h2 style='color: %s;'>Barangay Household Summary</h2>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>" +
                "<tr><td style='padding: 8px;'><b>Total Households:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Total Population:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Average Household Size:</b></td><td style='padding: 8px;'>%.1f persons</td></tr>" +
                "</table>" +
                "<br><b>Household Size Distribution:</b><br>" +
                "<table style='width: 100%%;'>%s</table>" +
                "</div></html>",
                String.format("#%02x%02x%02x", 
                    BarangayColors.PRIMARY_BLUE.getRed(),
                    BarangayColors.PRIMARY_BLUE.getGreen(),
                    BarangayColors.PRIMARY_BLUE.getBlue()),
                households, totalPopulation, avgHouseholdSize, sizeRows.toString()
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Household Summary Report", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private int countHouseholdHeads() {
            int count = 0;
            for (Resident r : reportResidents) {
                if (r.isHouseholdHead()) {
                    count++;
                }
            }
            return count;
        }
        
        private int calculateTotalPopulation() {
            int total = 0;
            for (Resident r : reportResidents) {
                if (r.isHouseholdHead()) {
                    total += r.getHouseholdSize();
                }
            }
            return total;
        }
        
        private double calculateAverageAge(List<Resident> residentsList) {
            if (residentsList.isEmpty()) return 0;
            int sum = 0;
            for (Resident r : residentsList) {
                sum += r.getAge();
            }
            return Math.round((double) sum / residentsList.size() * 10.0) / 10.0;
        }
    }