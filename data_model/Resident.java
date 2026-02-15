package data_model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Resident {
    
    public enum ResidentStatus {
        ACTIVE, INACTIVE
    }

    private String residentID;
    private String lastName;
    private String firstName;
    private String middleName;
    private String suffix;
    private String gender;
    private LocalDate dateofBirth;
    private String civilStatus;
    private String address;
    private String contactNumber;
    private ResidentStatus status;

    private List <HouseholdMember> householdMembers;

    public Resident (String residentID, String lastName, String firstName, String middleName, String suffiix,
        String gender, LocalDate dateofBirth, String civilStatus, String address, String contactNumber, ResidentStatus status){

            this.residentID = residentID;
            this.lastName = lastName;
            this.firstName = firstName;
            this.middleName = middleName; 
            this.suffix = suffix;
            this.gender = gender;
            this.dateofBirth = dateofBirth;
            this.civilStatus = civilStatus;
            this.address = address;
            this.contactNumber = contactNumber;
            this.status = status;
            this.householdMembers = new ArrayList<>();
        }

    public String getResidentID() { return residentID; }
    public void setResidentID(String residentID) { this.residentID = residentID;}

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName;}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateofBirth; }
    public void setDateOfBirth(LocalDate dateofBirth) { this.dateofBirth = dateofBirth; }

    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public ResidentStatus getStatus() { return status; }
    public void setStatus(ResidentStatus status) { this.status = status; }

    public List<HouseholdMember> getHouseholdMembers() {
        return householdMembers;
    }

    public void addHouseholdMember(HouseholdMember member) {
        householdMembers.add(member);
    }

    public void removeHouseholdMember(HouseholdMember member) {
        householdMembers.remove(member);
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
}
