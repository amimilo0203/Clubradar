package Login.forms;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class infoWaypoint extends DefaultWaypoint {
    private final String name;
    private final String[] beschreibung;
    // The raw ratings string for this club (format: "author∞stars∞text¿...").
    // Not final because a new rating may be added while the program is running.
    private String bewertungen;

    public infoWaypoint(GeoPosition pos, String name, String[] beschreibung, String bewertungen) {
        super(pos);
        this.name = name;
        this.beschreibung = beschreibung;
        this.bewertungen = bewertungen;
    }

    public String getName() { return name; }
    public String[] getBeschreibung() { return beschreibung; }
    public String getBewertungen() { return bewertungen; }

    /**
     * Adds (or replaces) the rating of one user for this club and saves the
     * change so it survives a restart.
     *
     * @param author the username of the rater (max one rating per user)
     * @param stars  star rating from 1 to 5
     * @param text   optional free text
     */
    public void addRating(String author, int stars, String text) {
        // Work on the parsed list so we can enforce "one rating per user".
        List<Rating> ratings = RatingManager.parseRatings(bewertungen);
        RatingManager.addOrReplace(ratings, new Rating(author, stars, text));
        bewertungen = RatingManager.toStorageString(ratings);

        // Persist this club's ratings (keyed by the club name).
        RatingStore.getInstance().setRatings(name, bewertungen);
    }
}
