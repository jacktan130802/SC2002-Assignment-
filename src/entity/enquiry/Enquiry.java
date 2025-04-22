package entity.enquiry;

import entity.btoProject.BTOProject;
import entity.roles.Applicant;
import java.io.Serializable;

/**
 * Represents an enquiry submitted by an applicant regarding a BTO project.
 */
public class Enquiry implements Serializable {
    private static int idCounter = 1;
    private int enquiryID;
    private final Applicant applicant;
    private final BTOProject project;
    private String message;
    private String reply;

    /**
     * Constructs an Enquiry object.
     * @param applicant The applicant submitting the enquiry.
     * @param project The BTO project related to the enquiry.
     * @param message The enquiry message.
     */
    public Enquiry(Applicant applicant, BTOProject project, String message) {
        this.enquiryID = idCounter++;
        this.applicant = applicant;
        this.project = project;
        this.message = message;
        this.reply = null;
    }

    public int getEnquiryID() {
        return enquiryID;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isReplied() {
        return reply != null;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setEnquiryID(int id) {
        this.enquiryID = id;
    
        // Prevent future ID clashes
        if (id >= idCounter) {
            idCounter = id + 1;
        }
    }
    
    /**
     * Displays the details of the enquiry.
     */
    public void view() {
        System.out.println("--- Enquiry Details ---");
        System.out.println("ID: " + enquiryID);
        System.out.println("Applicant NRIC: " + applicant.getNRIC());
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Message: " + message);
        System.out.println("Status: " + (isReplied() ? "Replied" : "Open"));
        if (isReplied()) {
            System.out.println("Reply: " + reply);
        }
    }

    /**
     * Replies to the enquiry with a message.
     * @param replyMessage The reply message.
     */
    public void reply(String replyMessage) {
        this.reply = replyMessage;
        System.out.println("Successfully replied to the enquiry.");
    }
}
