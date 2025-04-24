package entity;

import enums.FlatType;
import java.io.Serializable;

/**
 * Represents a receipt generated for a successful flat booking.
 */
public class Receipt implements Serializable {
    private String receiptId;
    
    private String applicantName;
    private String nric;
    private int age;
    private String maritalStatus;
    private FlatType flatType;
    private String projectName;
    private String neighborhood;

    /**
     * Constructs a Receipt object using an application.
     * @param receiptId The unique ID of the receipt.
     * @param app The application associated with the receipt.
     */
    public Receipt(String receiptId, Application app) {
        this.receiptId = receiptId;
        this.applicantName = app.getApplicant().getName();
        this.nric = app.getApplicant().getNRIC();
        this.age = app.getApplicant().getAge();
        this.maritalStatus = app.getApplicant().getMaritalStatus().toString();
        this.flatType = app.getFlatType();
        this.projectName = app.getProject().getProjectName();
        this.neighborhood = app.getProject().getNeighborhood();
    }
    public Receipt(String receiptId, String applicantName, String nric, int age, String maritalStatus, String flatType, String projectName, String neighborhood) {
        this.receiptId = receiptId;
        this.applicantName = applicantName;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.flatType = FlatType.valueOf(flatType); // convert string to enum
        this.projectName = projectName;
        this.neighborhood = neighborhood;
    }
    
    // Getters


    public String getReceiptId() { return receiptId; }
    public String getApplicantName() { return applicantName; }
    public String getNric() { return nric; }
    public int getAge() { return age; }
    public String getMaritalStatus() { return maritalStatus; }
    public FlatType getFlatType() { return flatType; }
    public String getProjectName() { return projectName; }
    public String getNeighborhood() { return neighborhood; }
}
