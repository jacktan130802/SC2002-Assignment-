package utility;

import entity.Application;
import entity.Receipt;

import java.util.UUID;

/**
 * Generates PDF-style receipts for applications.
 */
public class PDFReceiptGenerator extends ReceiptGenerator {

    /**
     * Generates a receipt for the given application in a PDF format.
     *
     * @param app The application for which the receipt is generated.
     * @return The generated receipt.
     */
    @Override
    public Receipt generate(Application app) {
        String receiptId = "PDF-" + UUID.randomUUID();  // Prefix to distinguish PDF format
        Receipt r = new Receipt(receiptId, app);

        // Simulate PDF generation logic (if required, include actual PDF file creation)
        System.out.println("Generating PDF receipt for application ID: ");

        app.setReceiptId(receiptId);
        app.setReceipt(r);
        return r;
    }
}
