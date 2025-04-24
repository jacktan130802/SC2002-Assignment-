package utility;

import entity.Application;
import entity.Receipt;

/**
 * An abstract class for generating receipts, implements the IReceiptGenerator interface.
 */
public abstract class ReceiptGenerator implements IReceiptGenerator {
    
    /**
     * Generates a receipt for the given application.
     *
     * @param app The application.
     * @return The generated Receipt.
     */
    @Override
    public abstract Receipt generate(Application app);  // Still abstract for subclass to implement
}
