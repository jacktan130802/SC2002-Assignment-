package entity;

import entity.btoProject.BTOProject;
import entity.roles.Applicant;
import enums.ApplicationStatus;
import enums.FlatType;
import java.io.Serializable;


public class Application implements Serializable {
    private Applicant applicant;
    private BTOProject project;
    private ApplicationStatus status;
    private FlatType flatType;
    private int applicationId;
    private static int nextId = 1;
    private boolean withdrawalRequested;

    private String receiptId;
    private Receipt receipt;

    public void setReceipt(Receipt r) {
        this.receipt = r;
        this.receiptId = r.getReceiptId();
    }

    public Receipt getReceipt() {
        return receipt;
    }
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    

    public String getReceiptId() {
        return receiptId;
    }

    public int getApplicationId() {
    return applicationId;
}

public void setApplicationId(int id) {
    this.applicationId = id;
}

    public Application(Applicant applicant, BTOProject project, FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.applicationId = nextId++;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setWithdrawalRequested(boolean value) {
        this.withdrawalRequested = value;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public BTOProject getProject() {
        return project;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public boolean isWithdrawalRequested() { 
        return withdrawalRequested; 
    }
}