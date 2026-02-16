package user_management;

public class ResidentUser extends User {
    private int residentID;
    
    public ResidentUser(String username, String password, String email, String phone, int residentID) {
        super(username, password, "RESIDENT", email, phone);
        this.residentID = residentID;
    }
    
    public int getResidentID() { return residentID; }
    
    @Override
    public boolean canAccessAdminPanel() { return false; }
    
    @Override
    public boolean canManageUsers() { return false; }
}
