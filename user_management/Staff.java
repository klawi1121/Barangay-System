package user_management;

public class Staff extends User {

    public Staff(String username, String password, String email, String phone) {
        super(username, password, "STAFF", email, phone);
    }

    @Override
    public boolean canAccessAdminPanel() { 
        return true; 
    }

    @Override
    public boolean canManageUsers() { 
        return false; 
    }

    @Override
    public boolean canViewAllResidents() { 
        return true; 
    }
}
