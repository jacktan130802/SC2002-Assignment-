package entity.btoProject;

import entity.roles.HDBOfficer;

/**
 * Represents a project that has been approved by an HDB officer.
 */
public class ApprovedProject {
    private String id;
    private BTOProject project;
    private HDBOfficer officer;

    /**
     * Constructs an ApprovedProject instance.
     *
     * @param id      The unique ID of the approved project.
     * @param project The associated BTO project.
     * @param officer The HDB officer who approved the project.
     */
    public ApprovedProject(String id, BTOProject project, HDBOfficer officer) {
        this.id = id;
        this.project = project;
        this.officer = officer;
    }

    /**
     * Gets the unique ID of the approved project.
     *
     * @return The ID of the approved project.
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
     * Gets the HDB officer who approved the project.
     *
     * @return The HDB officer.
     */
    public HDBOfficer getOfficer() {
        return officer;
    }
}
