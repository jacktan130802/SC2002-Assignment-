package controller;

import entity.Application;

import enums.*;

/**
 * Handles the generation of receipts for successful flat bookings.
 */
public class ReceiptController {
    /**
     * Generates and displays a receipt for a booked application.
     * @param app The application for which the receipt is generated.
     */
    public void generateReceipt(Application app) {
        if (app.getStatus() == ApplicationStatus.BOOKED) {
            System.out.println("--- Receipt ---");
            System.out.println("NRIC: " + app.getApplicant().getNRIC());
            System.out.println("Age: " + app.getApplicant().getAge());
            System.out.println("Flat Type: " + app.getFlatType());
            System.out.println("Project: " + app.getProject().getProjectName());
        }
    }
}
