package Login.forms;

import Login.forms.Club;
import Login.forms.infoWaypoint;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class ClubMap extends JFrame {

    private final Club club;
    private final ClubOverview clubOverview;
    private final Set<Waypoint> waypoints = new HashSet<>();
    private JSplitPane splitPane;

    public ClubMap() {
        setTitle("Karte");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JXMapViewer mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(4);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(new GeoPosition(51.195457, 6.428547));

        for (clubinfos club : ClubDatabase.getClubs()) {
            waypoints.add(club.getClubinfo());
            System.out.println("Marker hinzugefügt: " + club.getClubinfo().getName());
        }

        WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(painter);

        club = new Club();

        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                null,
                mapViewer
        );

        clubOverview = new ClubOverview(club, splitPane, mapViewer);
        splitPane.setLeftComponent(clubOverview);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.0);

        add(splitPane, BorderLayout.CENTER);

        PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener((MouseListener) panListener);
        mapViewer.addMouseMotionListener((MouseMotionListener) panListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
        mapViewer.setFocusable(true);

        JButton zoomIn  = new JButton("+");
        JButton zoomOut = new JButton("-");
        zoomIn.addActionListener(e -> {
            if (mapViewer.getZoom() > 1) mapViewer.setZoom(mapViewer.getZoom() - 1);
            System.out.println(mapViewer.getZoom());
        });
        zoomOut.addActionListener(e -> {
            if (mapViewer.getZoom() < 17) mapViewer.setZoom(mapViewer.getZoom() + 1);
            System.out.println(mapViewer.getZoom());
        });
        JPanel zoomPanel = new JPanel();
        zoomPanel.add(zoomIn);
        zoomPanel.add(zoomOut);
        add(zoomPanel, BorderLayout.SOUTH);

        //Beginnend von hier bis zur Anmerkung Ende wurde der Code mit Cascade ai SWE-1.6 erstellt
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Waypoint waypoint : waypoints) {
                    Point2D waypointPoint = mapViewer.getTileFactory()
                            .geoToPixel(waypoint.getPosition(), mapViewer.getZoom());
                    Point2D centerPoint = mapViewer.getTileFactory()
                            .geoToPixel(mapViewer.getCenterPosition(), mapViewer.getZoom());

                    int dx = (int)(waypointPoint.getX() - centerPoint.getX()) + mapViewer.getWidth() / 2;
                    int dy = (int)(waypointPoint.getY() - centerPoint.getY()) + mapViewer.getHeight() / 2;

                    if (Math.abs(e.getX() - dx) < 15 && Math.abs(e.getY() - dy) < 15) {
                        //Ende des KI generierten Inhalts
                        System.out.println("Übertrage Infos");
                        club.zeigeWaypoint((infoWaypoint) waypoint);
                        splitPane.setLeftComponent(club);
                        splitPane.revalidate();
                        splitPane.repaint();
                        break;
                    }
                }
            }
        });

        setVisible(true);
    }
}