package Login.forms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static String erstelleHash(String wort) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(wort.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Fehler beim Erstellen des Hashes", e);
        }
    }
}