package controller;

import entity.btoProject.BTOProject;
import entity.roles.HDBOfficer;

/**
 * Handles the registration and approval of HDB officers for BTO projects.
 */
public class OfficerRegistrationController {

    /**
     * Registers an officer for a BTO project.
     *
     * @param officer The officer to be registered.
     * @param project The BTO project for which the officer is registering.
     * @return {@code true} if the registration is successful, {@code false} otherwise.
     */
    public boolean registerOfficer(HDBOfficer officer, BTOProject project) {
        if (officer.hasAppliedTo(project) || !project.registerOfficer(officer)) return false;
        return true;
    }

    /**
     * Approves an officer for a BTO project.
     *
     * @param project The BTO project for which the officer is being approved.
     * @param officer The officer to be approved.
     */
    public void approveOfficer(BTOProject project, HDBOfficer officer) {
        if (project.approveOfficer(officer)) officer.approveForProject(project);
    }
}
