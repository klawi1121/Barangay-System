package data_model;

import java.time.LocalDate;
import java.time.Period;

public class HouseholdMember {
    private String lastName;
    private String firstName;
    private String qualifier;
    private LocalDate dateofBirth;
    private String civilStatus;

    public HouseholdMember(String lastName, String firstName, String qualifier, 
                            LocalDate dateofBirth, String civilStatus){
        
        this.lastName = lastName;
        this.firstName = firstName;
        this.qualifier = qualifier;
        this.dateofBirth = dateofBirth;
        this.civilStatus = civilStatus;
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getQualifier() { return qualifier; }
    public void setQualifier(String qualifier) { this.qualifier = qualifier; }
    
    public LocalDate getDateofBirth() { return dateofBirth;}
    public void setDateofBirth(LocalDate dateofBirth) { this.dateofBirth = dateofBirth;}

    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }
    
    public int getAge() {
        if (dateofBirth == null) 
            return 0;
        return Period.between(dateofBirth, LocalDate.now()).getYears();
    }   

    public String getFullName() {
        String name = lastName + ", " + firstName;
        if (qualifier != null && !qualifier.trim().isEmpty()) {
            name += " " + qualifier;
        }
        return name;
    }
}
