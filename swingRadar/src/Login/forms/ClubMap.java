package Login.forms;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class ClubMap extends JFrame{
    private JPanel panel1;

    public ClubMap(){
        setTitle("Karte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);

        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setLayout(new BorderLayout());

        JButton zoomIn  = new JButton("+");
        JButton zoomOut = new JButton("-");

        zoomIn.addActionListener(e -> mapViewer.setZoom(mapViewer.getZoom() - 1));
        zoomOut.addActionListener(e -> mapViewer.setZoom(mapViewer.getZoom() + 1));

        JPanel zoomPanel = new JPanel(new GridLayout(2, 1));
        zoomPanel.setOpaque(false); // transparent
        zoomPanel.add(zoomIn);
        zoomPanel.add(zoomOut);

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeft.setOpaque(false);
        topLeft.add(zoomPanel);
        mapViewer.add(topLeft, BorderLayout.NORTH);

        zoomIn.addActionListener(e -> {
            if (mapViewer.getZoom() > 1)
                mapViewer.setZoom(mapViewer.getZoom() - 1);
        });
        zoomOut.addActionListener(e -> {
            if (mapViewer.getZoom() < 5)
                mapViewer.setZoom(mapViewer.getZoom() + 1);
        });

        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(4); // schnelleres Laden
        mapViewer.setTileFactory(tileFactory);


        Set<Waypoint> waypoints = new HashSet<>();
        String[] Beschreibung = {"Adresse: Waldhausener Str. 40-42, 41061 Mönchengladbach-Nord", "Öffnungszeiten: Freitag,23:00–05:00 Samstag,23:00–05:00"};
        waypoints.add(new infoWaypoint(
                new GeoPosition(51.1950611,6.4285373),
                " ",
                Beschreibung
        ));
        waypoints.add(new infoWaypoint(
                new GeoPosition(50.9333, 6.9500),
                "Köln",
                Beschreibung
        ));

        WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(painter);

        PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Klickposition in Geo-Koordinaten umrechnen
                GeoPosition clickPos = mapViewer.convertPointToGeoPosition(e.getPoint());

                for (Waypoint waypoint : waypoints) {
                    // Waypoint-Position in Bildschirmpixel umrechnen
                    Point2D waypointPoint = mapViewer.getTileFactory()
                            .geoToPixel(waypoint.getPosition(), mapViewer.getZoom());
                    Point2D centerPoint = mapViewer.getTileFactory()
                            .geoToPixel(mapViewer.getCenterPosition(), mapViewer.getZoom());

                    // Offset berechnen
                    int dx = (int)(waypointPoint.getX() - centerPoint.getX()) + mapViewer.getWidth() / 2;
                    int dy = (int)(waypointPoint.getY() - centerPoint.getY()) + mapViewer.getHeight() / 2;

                    // Prüfen ob Klick nah genug am Marker (15px Radius)
                    if (Math.abs(e.getX() - dx) < 15 && Math.abs(e.getY() - dy) < 15) {
                        zeigeMarkerFenster(waypoint);
                        break;
                    }
                }
            }
        });

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        setLayout(new BorderLayout());
        add(mapViewer, BorderLayout.CENTER);
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClubMap::new);
    }

    private void zeigeMarkerFenster(Waypoint waypoint) {
        if (waypoint instanceof infoWaypoint iw) {
            JDialog dialog = new JDialog(this, iw.getName(), true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(this);

            JLabel nameLabel = new JLabel(iw.getName(), SwingConstants.CENTER);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));

            int i = 0;
            JTextArea[] beschreibung = {new JTextArea("hallo")};

            for(String e : iw.getBeschreibung()){
                beschreibung[i] = new JTextArea(e);
                beschreibung[i].setEditable(false);
                beschreibung[i].setLineWrap(true);
                beschreibung[i].setWrapStyleWord(true);
                System.out.println(e);
                System.out.println(beschreibung[i]);
            }
            //System.out.println(beschreibung.length);
            
            /*
            JTextArea beschreibung = new JTextArea(iw.getBeschreibung());
            beschreibung.setEditable(false);
            beschreibung.setLineWrap(true);
            beschreibung.setWrapStyleWord(true);
            */
            JButton schliessen = new JButton("Schließen");
            schliessen.addActionListener(e -> dialog.dispose());

            dialog.add(nameLabel, BorderLayout.NORTH);
            JTextArea test = new JTextArea("hallo");
            /*
            for(int j = 0; j < i; j++){
                if(beschreibung[0] == test)
                    break;
                dialog.add(new JScrollPane(test), BorderLayout.CENTER);
            }
             */
            dialog.add(new JScrollPane(beschreibung[0]), BorderLayout.CENTER);
            dialog.add(new JScrollPane(test), BorderLayout.CENTER);
            dialog.add(schliessen, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    }

    public static class InfoWaypoint {
    }
}
