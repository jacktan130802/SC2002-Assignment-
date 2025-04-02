package controller;

import entity.Application;
import entity.btoProject.BTOProject;

import entity.roles.*;
import enums.*;


public class ApplicationController 
{
    public boolean apply(Applicant applicant, BTOProject project, FlatType type) {
        if (applicant.getApplication() != null) return false;
        if (type == FlatType.THREE_ROOM && applicant.getMaritalStatus() == MaritalStatus.SINGLE)
            return false;
        Application app = new Application(applicant, project, type);
        applicant.setApplication(app);
        return true;
    }

    public void withdraw(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) return;
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
    }

    public void updateStatus(Application app, ApplicationStatus newStatus) {
        app.setStatus(newStatus);
    }
}