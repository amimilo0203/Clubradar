package Login.forms;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class infoWaypoint extends DefaultWaypoint {
    private final String name;
    private final String beschreibung;
    //private final int size;

    public infoWaypoint(GeoPosition pos, String name, String beschreibung) {
        super(pos);
        this.name = name;
        this.beschreibung = beschreibung;
        //this.size = this.beschreibung.length;
    }

    public String getName() { return name; }
    public String getBeschreibung() { return beschreibung; }
    //public int getSize() {return size;}
}
