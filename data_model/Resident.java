package data_model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Resident {
    
    public enum ResidentStatus {
        ACTIVE, DECEASED
    }

    private int residentID;
    private String lastName;
    private String firstName;
    private String middleName;
    private String suffix;
    private String sex;
    private LocalDate dateofBirth;
    private String medicalCondition;
    private int incomeBracket;
    private String motherTongue;
    private String religion;
    private String employment;
    private String maritalStatus;
    private String address;
    private String position; // Household Head, Spouse, Child, etc.
    private String contactNumber;
    private String occupation;
    private int householdHeadID; // ID of the household head (0 if self is head)
    private boolean isHouseholdHead;
    private ResidentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deceasedAt;
    private List<HouseholdMember> householdMembers;

    public Resident(int residentID, String lastName, String firstName, String middleName, String suffix,
                     LocalDate dateofBirth, String sex, String medicalCondition,
                   int incomeBracket, String motherTongue, String religion,
                   String employment, String maritalStatus, String address,
                   String position, String contactNumber, String occupation) {
            this.residentID = residentID;
            this.lastName = lastName;
            this.firstName = firstName;
            this.middleName = middleName; 
            this.suffix = suffix;
            this.sex = sex;
            this.dateofBirth = dateofBirth;
            this.medicalCondition = medicalCondition;
            this.incomeBracket = incomeBracket;
            this.motherTongue = motherTongue;
            this.religion = religion;
            this.employment = employment;
            this.maritalStatus = maritalStatus;
            this.address = address;
            this.position = position;
            this.contactNumber = contactNumber;
            this.occupation = occupation;
            this.householdHeadID = 0;
            this.isHouseholdHead = true;
            this.status = ResidentStatus.ACTIVE;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.householdMembers = new ArrayList<>();
        }

    public int getResidentID() { return residentID; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { 
        this.middleName = middleName; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { 
        this.suffix = suffix; 
        this.updatedAt = LocalDateTime.now();
    }
    public LocalDate getDateofBirth() { return dateofBirth; }
    public void setDateofBirth(LocalDate dateofBirth) { 
        this.dateofBirth = dateofBirth; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getSex() { return sex; }
    public void setSex(String sex) { 
        this.sex = sex; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { 
        this.medicalCondition = medicalCondition; 
        this.updatedAt = LocalDateTime.now();
    }
    public int getIncomeBracket() { return incomeBracket; }
    public void setIncomeBracket(int incomeBracket) { 
        this.incomeBracket = incomeBracket; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getMotherTongue() { return motherTongue; }
    public void setMotherTongue(String motherTongue) { 
        this.motherTongue = motherTongue; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getReligion() { return religion; }
    public void setReligion(String religion) { 
        this.religion = religion; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getEmployment() { return employment; }
    public void setEmployment(String employment) { 
        this.employment = employment; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { 
        this.maritalStatus = maritalStatus; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getAddress() { return address; }
    public void setAddress(String address) { 
        this.address = address; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getPosition() { return position; }
    public void setPosition(String position) { 
        this.position = position; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { 
        this.contactNumber = contactNumber; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { 
        this.occupation = occupation; 
        this.updatedAt = LocalDateTime.now();
    }
    public int getHouseholdHeadID() { return householdHeadID; }
    public void setHouseholdHeadID(int householdHeadID) { 
        this.householdHeadID = householdHeadID; 
        this.updatedAt = LocalDateTime.now();
    }
    public boolean isHouseholdHead() { return isHouseholdHead; }
    public void setHouseholdHead(boolean isHouseholdHead) { 
        this.isHouseholdHead = isHouseholdHead; 
        this.updatedAt = LocalDateTime.now();
    }
    public ResidentStatus getStatus() { return status; }
    public void setStatus(ResidentStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
        if (status == ResidentStatus.DECEASED) {
            this.deceasedAt = LocalDateTime.now();
        }
    }
    public LocalDateTime getDeceasedAt() { return deceasedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<HouseholdMember> getHouseholdMembers() { return householdMembers; }
    public void setHouseholdMembers(List<HouseholdMember> members) { 
        this.householdMembers = members; 
        this.updatedAt = LocalDateTime.now();
    }
    public void addHouseholdMember(HouseholdMember member) {
        this.householdMembers.add(member);
        this.updatedAt = LocalDateTime.now();
    }

    public int getAge() {
        if (dateofBirth == null)
            return 0;
        return java.time.Period.between(dateofBirth, LocalDate.now()).getYears();
    }

    public String getFullName() {
        String name = lastName + ", " + firstName;
        if (middleName != null && !middleName.isEmpty()) {
            name += " " + middleName;
        }
        if (suffix != null && !suffix.isEmpty()) {
            name += " " + suffix;
        }
        return name;
    }

    public int getHouseholdSize() {
        return householdMembers.size() + 1; // +1 for the head
    }
    
    public boolean isActive() {
        return status == ResidentStatus.ACTIVE;
    }
}
