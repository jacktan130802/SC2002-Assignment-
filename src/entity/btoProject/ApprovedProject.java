package entity.btoProject;

import entity.roles.HDBOfficer;

public class ApprovedProject {
    private String id;
    private BTOProject project;
    private HDBOfficer officer;

    public ApprovedProject(String id, BTOProject project, HDBOfficer officer) {
        this.id = id;
        this.project = project;
        this.officer = officer;
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
}
