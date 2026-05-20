package Login.forms;

import java.util.ArrayList;

public class Database {
    private static Database database;
    private ArrayList<Layer8> benutzer = new ArrayList<>();

    private Database() {}

    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }
    public void hinzufuegen(Layer8 b) { benutzer.add(b); }
    public ArrayList<Layer8> getAlle()      { return benutzer; }

    public Layer8 sucheNachName(String name) {
        return benutzer.stream()
                .filter(b -> b.getusername().equals(name))
                .findFirst()
                .orElse(null);
    }
}
