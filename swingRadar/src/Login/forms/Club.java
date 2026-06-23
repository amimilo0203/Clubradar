package Login.forms;

import javax.swing.*;
import java.awt.*;

public class Club extends JPanel {
    private JPanel mainPanel;
    private JLabel clubName;
    private JLabel oefnungszeiten;
    private JLabel adresse;
    private JLabel Sperator1;
    private JLabel Website;
    private String[] beschreibung;

    public Club(){
        clubName.setText(" ");
        oefnungszeiten.setText(" ");
        adresse.setText(" ");
        Sperator1.setText(" ");
        Website.setText(" ");
        System.out.println(clubName.getText());
        add(mainPanel, BorderLayout.CENTER);
    }

    public void zeigeWaypoint(infoWaypoint waypoint) {
        beschreibung = waypoint.getBeschreibung();
        System.out.println("Anzeigen der Informationen");
        System.out.println(waypoint.getBeschreibung());
        clubName.setText(waypoint.getName());
        adresse.setText(beschreibung[0]);
        oefnungszeiten.setText(beschreibung[1]);
        Website.setText(beschreibung[2]);
        revalidate();
        repaint();
    }
}
