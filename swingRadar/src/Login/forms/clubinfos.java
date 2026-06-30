package Login.forms;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

public class  clubinfos {
    public String[] beschreibung;
    public infoWaypoint clubinfo;

    clubinfos(String n, double x, double y, String[] beschreibung, String bewertungen){
        this.beschreibung = beschreibung;
        String persisted = RatingStore.getInstance().getRatings(n);
        String effective = (persisted != null) ? persisted : bewertungen;

        clubinfo = new infoWaypoint(new GeoPosition(x, y), n, beschreibung, effective);
    }
    public infoWaypoint getClubinfo() {
        return clubinfo;
    }
    public String getBewertungen() {
        return clubinfo.getBewertungen();
    }
}
