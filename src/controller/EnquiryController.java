package controller;

import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import entity.roles.Applicant;

import java.util.List;

public class EnquiryController {
    public void submitEnquiry(Applicant applicant, BTOProject project, String message) {
        Enquiry enquiry = new Enquiry(applicant, project, message);
        applicant.getEnquiries().add(enquiry);
    }

    public void replyToEnquiry(Enquiry enquiry, String reply) {
        enquiry.setReply(reply);
    }

    public void viewEnquiries(List<Enquiry> list) {
        list.forEach(Enquiry::view);
    }

    // Add these:
    public void editEnquiry(Enquiry enquiry, String newMessage) {
        enquiry.setMessage(newMessage);
    }

    public void deleteEnquiry(Applicant applicant, Enquiry enquiry) {
        applicant.getEnquiries().remove(enquiry);
    }
}
