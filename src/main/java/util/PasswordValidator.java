package util;

import java.util.Locale;

public class PasswordValidator {

    private PasswordValidator() {}

    public static String validate(String password, String nome, String cognome, String username) {
        if (password == null) return "Password mancante";

        if (password.length() < 8) {
            return "deve contenere almeno 8 caratteri";
        }

        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");

        if (!(hasLower && hasUpper && hasDigit && hasSpecial)) {
            return "deve includere maiuscola, minuscola, numero e carattere speciale";
        }

        String p = password.toLowerCase(Locale.ROOT);

        if (containsNonTrivial(p, nome) || containsNonTrivial(p, cognome) || containsNonTrivial(p, username)) {
            return "non deve contenere nome/cognome/username";
        }

        if (containsObviousSequence(p)) {
            return "non deve contenere sequenze ovvie (es. 1234, abcd)";
        }

        return null;
    }

    private static boolean containsNonTrivial(String passwordLower, String token) {
        if (token == null) return false;
        String t = token.trim().toLowerCase(Locale.ROOT);
        if (t.length() < 3) return false;
        return passwordLower.contains(t);
    }

    private static boolean containsObviousSequence(String p) {
        return p.contains("1234") || p.contains("2345") || p.contains("3456") || p.contains("4567") ||
                p.contains("5678") || p.contains("6789") ||
                p.contains("abcd") || p.contains("bcde") || p.contains("cdef") || p.contains("defg") ||
                p.contains("efgh") || p.contains("fghi") || p.contains("ghij");
    }
}
