package utility;

public class NRICValidator {
    public static boolean isValidNRIC(String nric) {
        // Null and length check (exactly 9 chars)
        if (nric == null || nric.length() != 9) {
            return false;
        }
    
        // First character must be a letter (S/T/F/G)
        char firstChar = Character.toUpperCase(nric.charAt(0));
        if (firstChar != 'S' && firstChar != 'T' && firstChar != 'F' && firstChar != 'G') {
            return false;
        }
    
        // Next 7 characters must be digits 
        for (int i = 1; i < 8; i++) {
            if (!Character.isDigit(nric.charAt(i))) {
                return false;
            }
        }
    
        // Last character must be a letter 
        char lastChar = nric.charAt(8);
        if (!Character.isLetter(lastChar)) {
            return false;
        }
    
        return true;
    }
}