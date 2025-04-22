package utility;

import entity.Application;
import entity.Receipt;

/**
 * Provides a method for generating receipts for applications.
 */
public interface IReceiptGenerator {

    /**
     * Generates a receipt for a given application.
     *
     * @param app The application for which the receipt is to be generated.
     * @return The generated {@link Receipt}.
     */
    Receipt generate(Application app);
}
