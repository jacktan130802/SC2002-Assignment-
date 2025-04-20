package utility;

import entity.Application;
import entity.Receipt;

public interface IReceiptGenerator {
    Receipt generate(Application app);
}
