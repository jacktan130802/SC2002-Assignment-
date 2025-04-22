package entity.btoProject;

import entity.roles.HDBOfficer;
import enums.OfficerRegistrationStatus;

/**
 * Represents a project that has been registered by an HDB officer.
 */
public class RegisteredProject {
    private String id;
    private BTOProject project;
    private HDBOfficer officer;
    private OfficerRegistrationStatus status;

    /**
     * Constructs a RegisteredProject instance.
     *
     * @param id      The unique ID of the registered project.
     * @param project The associated BTO project.
     * @param officer The HDB officer who registered the project.
     * @param status  The registration status of the project.
     */
    public RegisteredProject(String id, BTOProject project, HDBOfficer officer, OfficerRegistrationStatus status) {
        this.id = id;
        this.project = project;
        this.officer = officer;
        this.status = status;
    }

    /**
     * Gets the unique ID of the registered project.
     *
     * @return The ID of the registered project.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the associated BTO project.
     *
     * @return The BTO project.
     */
    public BTOProject getProject() {
        return project;
    }

    /**
     * Gets the HDB officer who registered the project.
     *
     * @return The HDB officer.
     */
    public HDBOfficer getOfficer() {
        return officer;
    }

    /**
     * Gets the registration status of the project.
     *
     * @return The registration status.
     */
    public OfficerRegistrationStatus getStatus() {
        return status;
    }

    /**
     * Sets the registration status of the project.
     *
     * @param status The new registration status.
     */
    public void setStatus(OfficerRegistrationStatus status) {
        this.status = status;
    }
}
