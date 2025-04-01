package entity.roles;


import enums.MaritalStatus;

public abstract class User{
    // all attributes access level = protected, so subclasses can inherit
    protected String name;
    protected String NRIC;
    protected String password;
    protected int age;
    protected MaritalStatus maritalStatus;

    // constructor
    public User(String NRIC, String password, int age, MaritalStatus maritalStatus) {
        this.NRIC = NRIC;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getNRIC() {
        return NRIC;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public String getPassword() {
        return password;
    }

    // setting the new password
    public void changePassword(String newPassword) 
    {
        this.password = newPassword;
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }


    public abstract void displayMenu();
    // not sure if needed, if login, logout and changing password is via login menu
    // public boolean login(String NRIC,String loginPass) {
    //     if (NRIC.equals(UserID)&&loginPass.equals(password)) {return true;}
    //     else {return false;}
    // }

    // public void logout() {}
    
    // public void changePassword() {
    //     Scanner sc = new Scanner(System.in);
    //     System.out.print("Enter new password: ");
    //     String newPassword = sc.next();
        
    //     this.password = newPassword;
    //     System.out.println("Password successfully changed.");
    //     // logout afterwards
    // }
}