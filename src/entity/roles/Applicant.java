package entity.roles;

import entity.Application;
import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.FlatType;
import enums.MaritalStatus;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Applicant extends User {

    private Application application;
    private List<Enquiry> enquiries = new ArrayList<>();

    public Applicant(String NRIC, String password, int age, MaritalStatus maritalStatus) {
        super(NRIC, password, age, maritalStatus);
    }

    public String getNRIC() {
        return super.getNRIC();
    }

    public void projectView(BTOProject p) {
        System.out.println("Project: " + p.getProjectName());
        System.out.println("Location: " + p.getNeighborhood());
        System.out.println("2-Room Units: " + p.getTwoRoomUnits());
        System.out.println("3-Room Units: " + p.getThreeRoomUnits());
    }

    public void projectApply(BTOProject p, FlatType type) {
        if (application != null) {
            System.out.println("You have already applied to a project.");
            return;
        }
        if (maritalStatus == MaritalStatus.SINGLE && type == FlatType.THREE_ROOM) {
            System.out.println("Single applicants cannot apply for 3-Room flats.");
            return;
        }
        application = new Application(this, p, type);
        System.out.println("Application submitted for project: " + p.getProjectName());
    }

    public void projectWithdraw() {
        if (application == null) {
            System.out.println("No application to withdraw.");
            return;
        }
        if (application.getStatus() == ApplicationStatus.BOOKED) {
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Booking withdrawn. Status set to UNSUCCESSFUL.");
        } else {
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application withdrawn successfully.");
        }
    }

    public void enquirySubmit(String message, BTOProject project) {
        Enquiry e = new Enquiry(this, project, message);
        enquiries.add(e);
        System.out.println("Enquiry submitted with ID: " + e.getEnquiryID());
    }

    public void enquiryView() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter ID of enquiry you want to view: ");
        int enqId = sc.nextInt();

        boolean found = false;
        for (Enquiry e : enquiries) {
            if (e.getEnquiryID() == enqId) {
                System.out.println("Message: " + e.getMessage());
                System.out.println("Reply: " + (e.isReplied() ? e.getReply() : "[No reply yet]"));
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Could not find an enquiry with that ID!");
        }
    }

    public void enquiryEdit(int enqId, String newMessage) {
        for (Enquiry e : enquiries) {
            if (e.getEnquiryID() == enqId) {
                if (!e.isReplied()) {
                    e.setMessage(newMessage);
                    System.out.println("Enquiry updated.");
                } else {
                    System.out.println("Cannot edit enquiry. Already replied.");
                }
                return;
            }
        }
        System.out.println("Enquiry ID not found.");
    }

    public void enquiryDelete(int enqId) {
        Iterator<Enquiry> iter = enquiries.iterator();
        while (iter.hasNext()) {
            Enquiry e = iter.next();
            if (e.getEnquiryID() == enqId) {
                if (!e.isReplied()) {
                    iter.remove();
                    System.out.println("Enquiry deleted.");
                } else {
                    System.out.println("Cannot delete enquiry. Already replied.");
                }
                return;
            }
        }
        System.out.println("Enquiry ID not found.");
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public boolean hasAppliedTo(BTOProject project) {
        return application != null && application.getProject().equals(project);
    }
    

    public void displayMenu() {
        System.out.println("--- Applicant Menu ---");
        System.out.println("1. View Projects\n2. Apply for BTO\n3. View Application\n4. Submit/View/Edit/Delete Enquiries\n5. Withdraw Application");
    }
}
