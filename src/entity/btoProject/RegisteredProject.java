package entity.btoProject;

import entity.roles.HDBOfficer;
import enums.OfficerRegistrationStatus;

public class RegisteredProject {
    private String id;
    private BTOProject project;
    private HDBOfficer officer;
    private OfficerRegistrationStatus status;

    public RegisteredProject(String id, BTOProject project, HDBOfficer officer, OfficerRegistrationStatus status) {
        this.id = id;
        this.project = project;
        this.officer = officer;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public BTOProject getProject() {
        return project;
    }

    public HDBOfficer getOfficer() {
        return officer;
    }

    public OfficerRegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(OfficerRegistrationStatus status) {
        this.status = status;
    }
}
