package Login.forms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent storage für club ratings
 */
public class RatingStore {
    private static RatingStore instance;
    private final Map<String, String> ratingsByClub = new LinkedHashMap<>();

    private final File ratingFile = new File("ratings.json");

    private RatingStore() {
        load();
    }

    public static RatingStore getInstance() {
        if (instance == null) {
            instance = new RatingStore();
        }
        return instance;
    }

    public String getRatings(String clubName) {
        return ratingsByClub.get(clubName);
    }

    public void setRatings(String clubName, String rawRatings) {
        ratingsByClub.put(clubName, rawRatings);
        save();
    }

    // Gleiche Persistance-Logik, die für User angelegt ist.
    // Diese wird recycled, somit ist sie jedes Mal als KI-generiert gekennzeichnet.

    // --------------------------------------
    // Ab hier von Claude Opus 4.8 erstellt.
    // --------------------------------------

    private void load() {
        if (!ratingFile.exists()) {
            return;
        }
        try {
            String json = new String(Files.readAllBytes(ratingFile.toPath()), StandardCharsets.UTF_8);
            for (String object : splitObjects(json)) {
                String club = extractString(object, "club");
                String ratings = extractString(object, "ratings");
                if (club != null && ratings != null) {
                    ratingsByClub.put(club, ratings);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read rating file: " + e.getMessage());
        }
    }

    private void save() {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        int i = 0;
        for (Map.Entry<String, String> entry : ratingsByClub.entrySet()) {
            json.append("  {\n");
            json.append("    \"club\": \"").append(escape(entry.getKey())).append("\",\n");
            json.append("    \"ratings\": \"").append(escape(entry.getValue())).append("\"\n");
            json.append("  }");
            if (i < ratingsByClub.size() - 1) {
                json.append(","); // comma between objects, but not after the last
            }
            json.append("\n");
            i++;
        }
        json.append("]\n");

        try {
            Files.write(ratingFile.toPath(), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not save rating file: " + e.getMessage());
        }
    }

    // --- tiny JSON helpers (same simple approach as UserManager) -----------

    /** Splits the top-level array into the text of each "{ ... }" object. */
    private List<String> splitObjects(String json) {
        List<String> objects = new ArrayList<>();
        int i = 0;
        while (true) {
            int start = json.indexOf('{', i);
            if (start == -1) break;

            int depth = 0;
            int end = -1;
            for (int j = start; j < json.length(); j++) {
                char c = json.charAt(j);
                if (c == '{') depth++;
                else if (c == '}') {
                    depth--;
                    if (depth == 0) { end = j; break; }
                }
            }
            if (end == -1) break;

            objects.add(json.substring(start, end + 1));
            i = end + 1;
        }
        return objects;
    }

    /** Reads the string value of "key" out of a single JSON object. */
    private String extractString(String object, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = object.indexOf(search);
        if (keyIndex == -1) return null;

        int valueStart = object.indexOf('"', keyIndex + search.length());
        if (valueStart == -1) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = valueStart + 1; i < object.length(); i++) {
            char c = object.charAt(i);
            if (c == '\\' && i + 1 < object.length()) {
                char next = object.charAt(++i); // un-escape the following character
                switch (next) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default:  sb.append(next); // covers \" and \\
                }
            } else if (c == '"') {
                break; // closing quote -> value complete
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Escapes the characters that would otherwise break a JSON string. */
    private String escape(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }
}
