package controller;

import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import entity.roles.Applicant;

import java.util.List;

/**
 * Controller class for managing enquiries.
 * This class provides methods for submitting, replying, editing, and deleting enquiries.
 */
public class EnquiryController {

    /**
     * Submits a new enquiry for a specific project.
     *
     * @param applicant The applicant submitting the enquiry.
     * @param project   The project related to the enquiry.
     * @param message   The enquiry message.
     */
    public void submitEnquiry(Applicant applicant, BTOProject project, String message) {
        Enquiry enquiry = new Enquiry(applicant, project, message);
        applicant.getEnquiries().add(enquiry);
    }

    /**
     * Replies to an existing enquiry.
     *
     * @param enquiry The enquiry to reply to.
     * @param reply   The reply message.
     */
    public void replyToEnquiry(Enquiry enquiry, String reply) {
        enquiry.setReply(reply);
    }

    /**
     * Displays a list of enquiries.
     *
     * @param list The list of enquiries to view.
     */
    public void viewEnquiries(List<Enquiry> list) {
        list.forEach(Enquiry::view);
    }

    /**
     * Edits the message of an existing enquiry.
     *
     * @param enquiry    The enquiry to edit.
     * @param newMessage The new message for the enquiry.
     */
    public void editEnquiry(Enquiry enquiry, String newMessage) {
        enquiry.setMessage(newMessage);
    }

    /**
     * Deletes an enquiry from an applicant's list of enquiries.
     *
     * @param applicant The applicant who owns the enquiry.
     * @param enquiry   The enquiry to delete.
     */
    public void deleteEnquiry(Applicant applicant, Enquiry enquiry) {
        applicant.getEnquiries().remove(enquiry);
    }
}
