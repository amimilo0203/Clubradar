package Login.forms;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

public class  clubinfos {
    public String[] beschreibung;
    public infoWaypoint clubinfo;
    public static String bewertungen;

    clubinfos(String n, double x, double y,String[] beschreibung, String b){
        this.beschreibung = beschreibung;
        clubinfo = new infoWaypoint(new GeoPosition(x, y), n, beschreibung);
        this.bewertungen = b;
    }
    public infoWaypoint getClubinfo() {
        return clubinfo;
    }
    public static String getBewertungen() {
        if(bewertungen != null)
            return bewertungen;
        return null;
    }
}
