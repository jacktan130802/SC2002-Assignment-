package entity.user;

public class Manager {
    private String name;
    private String employeeId;
    private String contactInfo;

    public Manager(String name, String employeeId, String contactInfo) {
        this.name = name;
        this.employeeId = employeeId;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}