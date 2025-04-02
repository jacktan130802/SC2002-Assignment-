package controller;

import entity.btoProject.BTOProject;
import entity.roles.HDBOfficer;

public class OfficerRegistrationController{
    public boolean registerOfficer(HDBOfficer officer, BTOProject project) {
        if (officer.hasAppliedTo(project) || !project.registerOfficer(officer)) return false;
        return true;
    }

    public void approveOfficer(BTOProject project, HDBOfficer officer) {
        if (project.approveOfficer(officer)) officer.approveForProject(project);
    }
}