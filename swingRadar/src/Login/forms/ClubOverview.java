package Login.forms;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class ClubOverview extends JPanel {
    private JPanel mainPanel;
    private Club clubanzeige1;
    private JSplitPane splitPane;
    private JPanel clubListPanel;

    public ClubOverview(Club club, JSplitPane splitPane, JXMapViewer mapViewer) {
        this.clubanzeige1 = club;
        this.splitPane = splitPane;
        
        setLayout(new BorderLayout());
        clubListPanel = new JPanel();
        clubListPanel.setLayout(new BoxLayout(clubListPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(clubListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        for (clubinfos clubInfo : ClubDatabase.getClubs()) {
            //Beginnend von hier bis zur Anmerkung Ende wurde der Code mit Cascade ai SWE-1.6 erstellt
            JLabel clubLabel = new JLabel(clubInfo.getClubinfo().getName());
            clubLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clubLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            clubLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, clubLabel.getPreferredSize().height));
            //Ende des KI generierten Inhalts
            
            clubLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    clubanzeige1.zeigeWaypoint(clubInfo.getClubinfo());
                    splitPane.setLeftComponent(clubanzeige1);
                    splitPane.revalidate();
                    splitPane.repaint();
                    mapViewer.setAddressLocation(clubInfo.getClubinfo().getPosition());
                    mapViewer.setZoom(2);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    clubLabel.setForeground(Color.BLUE);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    clubLabel.setForeground(Color.BLACK);
                }
            });
            
            clubListPanel.add(clubLabel);
            clubListPanel.add(Box.createVerticalStrut(5));
        }
        
        add(scrollPane, BorderLayout.CENTER);
    }
}
