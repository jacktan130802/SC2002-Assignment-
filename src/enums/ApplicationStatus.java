package enums;

/**
 * Represents the status of an application.
 */
public enum ApplicationStatus {
    /**
     * The application is pending review.
     */
    PENDING,

    /**
     * The application has been successfully processed.
     */
    SUCCESSFUL,

    /**
     * The application has been rejected.
     */
    UNSUCCESSFUL,

    /**
     * The application has been booked.
     */
    BOOKED,
}
