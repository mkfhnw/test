package server.services;

import common.Token;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/* InputValidator class
 * This class offers some methods to perform sanity checks on user inputs and any other kind of input validation.
 */
public class InputValidator {

    // Singleton field
    private static InputValidator inputValidator;
    private final List<String> categories = Arrays.asList("Geplant", "Wichtig", "Erledigt", "Papierkorb");

    // Private constructor since we work with a factory method
    public static InputValidator getInputValidator() {
        if(inputValidator == null) { inputValidator = new InputValidator(); }
        return inputValidator;
    }

    // Returns true if username contains an @ and does neither start nor end with a "."
    public boolean validateUsername(String username) {
        String[] usernameParts = username.split("@");
        return usernameParts.length != 0 && !usernameParts[0].startsWith(".") && !usernameParts[1].endsWith(".");
    }

    // Returns true if password is at least 3, at max 20 characters long.
    public boolean validatePassword(String password) {
        return password.length() >= 3 && password.length() <= 20;
    }

    // Returns false if the token expired
    public boolean isTokenStillAlive(Token token) {
        return (System.currentTimeMillis() / 1000L) < token.getUnixTimeOfDeath();
    }

    // Returns true if the input string can be converted to a date
    public boolean isDate(String dateString) {
        try {
            // If we can convert the 3 parameter to a LocalDate, it is in fact a date
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dateString, dateTimeFormatter);
            return true;
        } catch (Exception e) {
            // If we catch an exception, the string cannot be converted to a date
            return false;
        }
    }

    // Returns the parameter type in string format
    public String getParameterType(String parameter) {
        if (this.isDate(parameter)) { return "DueDate"; }
        if (this.categories.contains(parameter)) { return "Category"; }

        // If dueDate and category are still null, this means that both of the above checks failed. Therefore,
        // the parameter is a description
        if (parameter.length() <= 255) { return "Description"; }


        // If none of the checks above hit, return undefined
        return "Undefined";
    }


}
