package Login.forms;

import java.util.ArrayList;
import java.util.List;

//Rating management für Clubs

public class RatingManager {

    // delimiter als Konstanten
    private static final String RATING_SEPARATOR = "¿"; // zwischen zwei ratings
    private static final String FIELD_SEPARATOR = "∞";  // zwischen author/stars/text


    public static List<Rating> parseRatings(String raw) {
        List<Rating> ratings = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return ratings;
        }

        // Split in einzelne ratings zuerst; "\\s*" entfernt überschüssige Leerzeichen
        String[] entries = raw.split(RATING_SEPARATOR + "\\s*");
        for (String entry : entries) {
            if (entry.isBlank()) {
                continue;
            }

            String[] parts = entry.split(FIELD_SEPARATOR + "\\s*", 3);
            String author = parts[0].trim();
            int stars = 0;
            String text = "";
            if (parts.length == 3) {
                stars = parseStars(parts[1]);
                text = parts[2].trim();
            } else if (parts.length == 2) {
                Integer maybeStars = tryParseStars(parts[1].trim());
                if (maybeStars != null) {
                    stars = maybeStars;
                } else {
                    text = parts[1].trim();
                }
            }
            ratings.add(new Rating(author, stars, text));
        }
        return ratings;
    }

    /**
     * Fügt eine Bewertung hinzu ODER ersetzt eine bereits vorhandene Bewertung
     * desselben Autors. Dadurch gibt es maximal eine Bewertung pro Nutzer pro Club
     */
    public static void addOrReplace(List<Rating> ratings, Rating newRating) {
        ratings.removeIf(r -> r.getAuthor().equals(newRating.getAuthor()));
        ratings.add(newRating);
    }


    public static String toStorageString(List<Rating> ratings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ratings.size(); i++) {
            if (i > 0) {
                sb.append(RATING_SEPARATOR);
            }
            sb.append(ratings.get(i).toStorageString());
        }
        return sb.toString();
    }

    // --- kleine Helfer für die Sterne ---

    /** Parst die Sterne und liefert 0, wenn es keine gültige Zahl (1..5) ist */
    private static int parseStars(String value) {
        Integer stars = tryParseStars(value.trim());
        return stars != null ? stars : 0;
    }

    /** Gibt die Sterne 1..5 zurück oder null, wenn der Wert ungültig ist */
    private static Integer tryParseStars(String value) {
        try {
            int stars = Integer.parseInt(value);
            if (stars >= 1 && stars <= 5) {
                return stars;
            }
        } catch (NumberFormatException ignored) {
            // kein gültiger Integer -> null
        }
        return null;
    }
}
