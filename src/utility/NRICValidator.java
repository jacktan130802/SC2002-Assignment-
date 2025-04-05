package utility;

public class NRICValidator {
    public static boolean isValidNRIC(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }

        char firstChar = nric.charAt(0);
        if (firstChar != 'S' && firstChar != 'T' && firstChar != 'F' && firstChar != 'G') {
            return false;
        }

        for (int i = 1; i < 8; i++) {
            if (!Character.isDigit(nric.charAt(i))) {
                return false;
            }
        }

        char lastChar = nric.charAt(8);
        if (!Character.isUpperCase(lastChar)) {
            return false;
        }

        return true;
    }
}