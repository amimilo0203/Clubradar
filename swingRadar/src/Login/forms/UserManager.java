package Login.forms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *  zentrales User management
 *
 *  - hält die liste aller bekannten users in memory,
 *  - läd sie von einer JSON file bei start-up,
 *  - saved diese zurück zu dieser JSON file wenn ein neuer Nutzer registriert
 */
public class UserManager {

    private static UserManager instance;
    private final List<Layer8> users = new ArrayList<>();

    private Layer8 currentUser;
    private final File userFile = new File("users.json");

    private UserManager() {
        loadUsers();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public List<Layer8> getUsers() {
        return users;
    }

    public Layer8 findUserByName(String username) {
        for (Layer8 user : users) {
            if (user.getusername().equals(username)) {
                return user;
            }
        }
        return null;
    }


    public Layer8 registerUser(String username, String password, boolean stayLoggedIn, String role) {
        if (findUserByName(username) != null) {
            return null;
        }
        Layer8 user = new Layer8(username, password, stayLoggedIn, role);
        users.add(user);
        saveUsers();
        return user;
    }

    public boolean checkLogin(String username, String password) {
        Layer8 user = findUserByName(username);
        return user != null && user.checkPassword(password);
    }


    public void setCurrentUser(Layer8 user) {
        this.currentUser = user;
    }
    public Layer8 getCurrentUser() {
        return currentUser;
    }

    // --------------------------------------
    // Ab hier von Claude Opus 4.8 erstellt.
    // --------------------------------------

    private void loadUsers() {
        if (!userFile.exists()) {
            return;
        }
        try {
            String json = new String(Files.readAllBytes(userFile.toPath()), StandardCharsets.UTF_8);
            for (String object : splitObjects(json)) {
                String username = extractString(object, "username");
                String passwordHash = extractString(object, "passwordHash");
                String role = extractString(object, "role");
                boolean stayLoggedIn = extractBoolean(object, "stayLoggedIn");

                if (username == null || passwordHash == null) {
                    continue;
                }
                users.add(Layer8.fromStoredData(username, passwordHash, stayLoggedIn, role));
            }
        } catch (IOException e) {
            System.out.println("Could not read user file: " + e.getMessage());
        }
    }

    /** Writes all users to the JSON file. */
    private void saveUsers() {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < users.size(); i++) {
            Layer8 user = users.get(i);
            json.append("  {\n");
            json.append("    \"username\": \"").append(escape(user.getusername())).append("\",\n");
            // getpassword() returns the hash, never the plain text password
            json.append("    \"passwordHash\": \"").append(escape(user.getpassword())).append("\",\n");
            json.append("    \"stayLoggedIn\": ").append(user.getangemeldetBleiben()).append(",\n");
            json.append("    \"role\": \"").append(escape(user.getrole())).append("\"\n");
            json.append("  }");
            if (i < users.size() - 1) {
                json.append(","); // comma between objects, but not after the last
            }
            json.append("\n");
        }
        json.append("]\n");

        try {
            Files.write(userFile.toPath(), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not save user file: " + e.getMessage());
        }
    }

    // --- tiny JSON helpers -------------------------------------------------
    // These only need to understand the exact format saveUsers() produces.

    /** Splits the top-level array into the text of each "{ ... }" object. */
    private List<String> splitObjects(String json) {
        List<String> objects = new ArrayList<>();
        int i = 0;
        while (true) {
            int start = json.indexOf('{', i);
            if (start == -1) break;

            // Walk forward counting braces so nested "{...}" would be handled too.
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

        // The value starts at the first quote after the key (the ':' has none).
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
                break; // closing quote reached -> value complete
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Reads the boolean value of "key" out of a single JSON object. */
    private boolean extractBoolean(String object, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = object.indexOf(search);
        if (keyIndex == -1) return false;

        int colon = object.indexOf(':', keyIndex + search.length());
        if (colon == -1) return false;

        return object.substring(colon + 1).trim().startsWith("true");
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
