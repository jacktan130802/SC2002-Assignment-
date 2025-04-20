package utility;

import entity.Application;
import entity.Receipt;

import java.util.UUID;

public class StandardReceiptGenerator implements IReceiptGenerator {
    @Override
    public Receipt generate(Application app) {
        String receiptId = UUID.randomUUID().toString();
        Receipt r = new Receipt(receiptId, app);
        app.setReceiptId(receiptId);
        app.setReceipt(r);
        return r;
    }
}
