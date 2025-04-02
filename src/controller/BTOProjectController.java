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
        boolean isSingle = applicant.getMaritalStatus().toString().equalsIgnoreCase("SINGLE");
        int age = applicant.getAge();
    
        for (BTOProject p : projects) {
            if (!p.isVisible()) continue;
    
            if (isSingle && age >= 35 && p.hasTwoRoom()) {
                viewable.add(p);
            } else if (!isSingle && age >= 21 && (p.hasTwoRoom() || p.hasThreeRoom())) {
                viewable.add(p);
            }
        }
    
        return viewable;
    }
    
    

    public BTOProject getProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}