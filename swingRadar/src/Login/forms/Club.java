package Login.forms;

import javax.swing.*;

public class Club extends JPanel {
    private JPanel panel1;
    private JButton test1Button;
    private JTextField test3TextField;
    private JLabel test2;

    public Club(){
        test2.setText("hallo");
    }

    public void zeigeWaypoint(infoWaypoint waypoint) {
        System.out.println("Anzeigen der Informationen");
        System.out.println(waypoint.getBeschreibung());
        test2.setText(waypoint.getName());
        test3TextField.setText(waypoint.getBeschreibung());
    }
}
