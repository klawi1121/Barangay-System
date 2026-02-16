package data_storage_security;

import user_management.*;
import data_model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SecureFileHandler {
    private static final String RESIDENTS_FILE = "residents_secure.dat";
    private static final String USERS_FILE = "users_secure.dat";
    private static final String LOGS_FILE = "system_logs.dat";
    private static final String ARCHIVE_FILE = "deceased_archive.dat";
    private static final String COUNTER_FILE = "id_counter.dat";
    private static final String ENCRYPTION_KEY = "BARANGAY_SECURE_2024";

    private static int nextResidentID = 1; // Start from 1, will be formatted as 000001

    private static String encryptData(String data) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            encrypted.append((char) (data.charAt(i) ^ ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length())));
        }
        return encrypted.toString();
    }

    private static String decryptData(String data) {
        return encryptData(data);
    }

    // NEW: Get next resident ID in format 000001, 000002, etc.
    public static String getNextResidentIdFormatted() {
        return String.format("%06d", nextResidentID);
    }

    public static int getNextResidentId() {
        return nextResidentID;
    }

    public static void incrementResidentId() {
        nextResidentID++;
        saveCounter();
    }

    private static void saveCounter() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COUNTER_FILE))) {
            oos.writeInt(nextResidentID);
        } catch (IOException e) {
            System.err.println("Error saving counter: " + e.getMessage());
        }
    }

    private static void loadCounter() {
        File file = new File(COUNTER_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COUNTER_FILE))) {
                nextResidentID = ois.readInt();
            } catch (IOException e) {
                System.err.println("Error loading counter: " + e.getMessage());
            }
        }
    }

    public static void saveResidents(List<Resident> residents) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESIDENTS_FILE))) {
            List<String> encryptedList = new ArrayList<>();
            for (Resident resident : residents) {
                if (resident.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    String data = serializeResident(resident);
                    encryptedList.add(encryptData(data));
                }
            }
            oos.writeObject(encryptedList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving residents: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Resident> loadResidents() {
        loadCounter(); // Load the counter first
        List<Resident> residents = new ArrayList<>();
        File file = new File(RESIDENTS_FILE);
        if (!file.exists())
            return residents;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESIDENTS_FILE))) {
            List<String> encryptedList = (List<String>) ois.readObject();
            for (String encrypted : encryptedList) {
                String decrypted = decryptData(encrypted);
                Resident resident = deserializeResident(decrypted);
                if (resident != null && resident.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    residents.add(resident);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading residents: " + e.getMessage());
        }
        return residents;
    }

    public static void archiveDeceased(Resident resident) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVE_FILE, true))) {
            String data = serializeResident(resident);
            String encrypted = encryptData(data);
            oos.writeObject(encrypted);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error archiving deceased: " + e.getMessage());
        }
    }

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            List<String> encryptedList = new ArrayList<>();
            for (User user : users) {
                String data = serializeUser(user);
                encryptedList.add(encryptData(data));
            }
            oos.writeObject(encryptedList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            Admin defaultAdmin = new Admin("admin", "admin123", "admin@barangay.ph", "09123456789");
            users.add(defaultAdmin);
            saveUsers(users);
            return users;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            List<String> encryptedList = (List<String>) ois.readObject();
            for (String encrypted : encryptedList) {
                String decrypted = decryptData(encrypted);
                User user = deserializeUser(decrypted);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void logActivity(String username, String action) {
        try (FileWriter fw = new FileWriter(LOGS_FILE, true);
                BufferedWriter bw = new BufferedWriter(fw)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logEntry = encryptData(timestamp + "|" + username + "|" + action);
            bw.write(logEntry);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }

    public static List<String> getLoginHistory(String username) {
        List<String> history = new ArrayList<>();
        File file = new File(LOGS_FILE);
        if (!file.exists())
            return history;

        try (BufferedReader br = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String decrypted = decryptData(line);
                if (decrypted.contains(username)) {
                    history.add(decrypted);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading logs: " + e.getMessage());
        }
        return history;
    }

    private static String serializeResident(Resident resident) {
        // Serialize household members
        StringBuilder membersData = new StringBuilder();
        for (HouseholdMember member : resident.getHouseholdMembers()) {
            membersData.append(member.getLastName()).append(",");
            membersData.append(member.getFirstName()).append(",");
            membersData.append(member.getQualifier()).append(",");
            membersData.append(member.getAge()).append(",");
            membersData.append(member.getDateofBirth()).append(",");
            membersData.append(member.getCivilStatus()).append(";");
        }

        return String.join("|",
                String.valueOf(resident.getResidentID()),
                resident.getFirstName(),
                resident.getMiddleName(),
                resident.getLastName(),
                resident.getSuffix() != null ? resident.getSuffix() : "",
                String.valueOf(resident.getAge()),
                resident.getDateofBirth() != null ? resident.getDateofBirth().toString() : "",
                resident.getSex(),
                resident.getMedicalCondition(),
                String.valueOf(resident.getIncomeBracket()),
                resident.getMotherTongue(),
                resident.getReligion(),
                resident.getEmployment(),
                resident.getMaritalStatus(),
                resident.getAddress(),
                resident.getPosition(),
                resident.getContactNumber(),
                resident.getOccupation(),
                String.valueOf(resident.getHouseholdHeadID()),
                String.valueOf(resident.isHouseholdHead()),
                resident.getStatus() != null ? resident.getStatus().toString() : "",
                resident.getCreatedAt() != null ? resident.getCreatedAt().toString() : "",
                resident.getUpdatedAt() != null ? resident.getUpdatedAt().toString() : "",
                resident.getDeceasedAt() != null ? resident.getDeceasedAt().toString() : "",
                membersData.toString());

    }

    private static Resident deserializeResident(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 23)
                return null;

            Resident resident;
            int residentID = Integer.parseInt(parts[0]);
            String firstName = parts[1];
            String middleName = parts[2];
            String lastName = parts[3];
            String suffix = parts[4];
            // int age = Integer.parseInt(parts[5]);
            LocalDate dateofBirth = LocalDate.parse(parts[6]);
            String sex = parts[7];
            String medicalCondition = parts[8];
            int incomeBracket = Integer.parseInt(parts[9]);
            String motherTongue = parts[10];
            String religion = parts[11];
            String employment = parts[12];
            String maritalStatus = parts[13];
            String address = parts[14];
            String position = parts[15];
            String contactNumber = parts[16];
            String occupation = parts[17];
            int householdHeadID = Integer.parseInt(parts[18]);
            boolean isHouseholdHead = Boolean.parseBoolean(parts[19]);

            if (isHouseholdHead) {
                resident = new Resident(residentID, firstName, middleName, lastName, suffix,
                        dateofBirth, sex, medicalCondition, incomeBracket, motherTongue, religion,
                        employment, maritalStatus, address, position, contactNumber, occupation);
            } else {
                resident = new Resident(residentID, lastName, firstName, middleName, suffix,
                        dateofBirth, sex, medicalCondition, incomeBracket, motherTongue, religion,
                        employment, maritalStatus, address, position, contactNumber, occupation);
            }

            resident.setStatus(Resident.ResidentStatus.valueOf(parts[20]));

            // Deserialize household members
            if (parts.length > 23) {
                String membersData = parts[23];
                if (membersData != null && !membersData.isEmpty()) {
                    String[] members = membersData.split(";");

                    for (String memberData : members) {
                        if (memberData == null || memberData.isEmpty())
                            continue;

                        String[] memberParts = memberData.split(",", -1); // keep empty fields
                        if (memberParts.length >= 5) {

                            LocalDate dob = null;
                            String dobStr = memberParts[3].trim();
                            if (!dobStr.isEmpty()) {
                                try {
                                    dob = LocalDate.parse(dobStr); // expects yyyy-MM-dd
                                } catch (Exception ignored) {
                                    dob = null; // invalid date, fallback
                                }
                            }

                            HouseholdMember member = new HouseholdMember(
                                    memberParts[0].trim(), // lastName
                                    memberParts[1].trim(), // firstName
                                    memberParts[2].trim(), // qualifier
                                    dob, // dateOfBirth
                                    memberParts[4].trim() // civilStatus
                            );

                            resident.addHouseholdMember(member);
                        }
                    }
                }
            }

            return resident;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String serializeUser(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUsername()).append("|");
        sb.append(user.getEncryptedPassword()).append("|");
        sb.append(user.getRole()).append("|");
        sb.append(user.getEmail()).append("|");
        sb.append(user.getPhone()).append("|");
        sb.append(user.isActive()).append("|");

        if (user instanceof Admin) {
            sb.append("ADMIN");
        } else if (user instanceof ResidentUser) {
            sb.append("RESIDENT|").append(((ResidentUser) user).getResidentID());
        } else if (user instanceof Staff) {
            sb.append("STAFF");
        }

        return sb.toString();
    }

    private static User deserializeUser(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 6)
                return null;

            String username = parts[0];
            String password = parts[1];
            String role = parts[2];
            String email = parts[3];
            String phone = parts[4];
            boolean active = Boolean.parseBoolean(parts[5]);

            User user;
            switch (role) {
                case "ADMIN":
                    user = new Admin(username, "temp", email, phone);
                    user.setEncryptedPassword(password);
                    break;
                case "RESIDENT":
                    int residentID = parts.length > 7 ? Integer.parseInt(parts[7]) : 0;
                    user = new ResidentUser(username, "temp", email, phone, residentID);
                    user.setEncryptedPassword(password);
                    break;
                case "STAFF":
                    user = new Staff(username, "temp", email, phone);
                    user.setEncryptedPassword(password);
                    break;
                default:
                    return null;
            }
            user.setActive(active);
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}