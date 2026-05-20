package Login.forms;

import java.util.ArrayList;

public class Benutzerverwaltung {
    private static Benutzerverwaltung instanz;
    private ArrayList<Layer8> benutzer = new ArrayList<>();

    // Privater Konstruktor – niemand kann new Benutzerverwaltung() aufrufen
    private Benutzerverwaltung() {}

    // Gibt immer dieselbe Instanz zurück
    public static Benutzerverwaltung getInstanz() {
        if (instanz == null) {
            instanz = new Benutzerverwaltung();
        }
        return instanz;
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
