package entity;

import entity.btoProject.BTOProject;
import enums.ApplicationStatus;
import enums.FlatType;
import entity.roles.Applicant;
import java.io.Serializable;

public class Application implements Serializable {
    private Applicant applicant;
    private BTOProject project;
    private ApplicationStatus status;
    private FlatType flatType;

    public Application(Applicant applicant, BTOProject project, FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
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
}