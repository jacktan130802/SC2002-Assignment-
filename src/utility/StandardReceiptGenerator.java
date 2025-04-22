package utility;

import entity.Application;
import entity.Receipt;

import java.util.UUID;

/**
 * Generates standard receipts for applications.
 */
public class StandardReceiptGenerator implements IReceiptGenerator {

    /**
     * Generates a receipt for the given application.
     *
     * @param app The application for which the receipt is generated.
     * @return The generated receipt.
     */
    @Override
    public Receipt generate(Application app) {
        String receiptId = UUID.randomUUID().toString();
        Receipt r = new Receipt(receiptId, app);
        app.setReceiptId(receiptId);
        app.setReceipt(r);
        return r;
    }
}
