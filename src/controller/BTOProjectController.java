package controller;


import entity.btoProject.BTOProject;
import entity.roles.*;


import java.util.*;

public class BTOProjectController {
    private List<BTOProject> projects;

    public BTOProjectController(List<BTOProject> projects) {
        this.projects = projects;
    }

    public List<BTOProject> getVisibleProjectsFor(Applicant applicant) {
        List<BTOProject> viewable = new ArrayList<>();
        for (BTOProject p : projects) {
            if (p.isVisible()) viewable.add(p);
        }
        return viewable;
    }

    public BTOProject getProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}