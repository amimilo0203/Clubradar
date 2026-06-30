package Login.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;

public class Club extends JPanel {
    private JPanel mainPanel;
    private JLabel clubName;
    private JTextArea oefnungszeiten;
    private JLabel adresse;
    private JLabel Sperator1;
    private JLabel Website;
    private JTextArea Bewertungen;
    private String[] beschreibung;

    private infoWaypoint currentWaypoint;
    private JSplitPane splitPane;
    private Component clubOverview;

    public Club(){
        clubName.setText(" ");
        oefnungszeiten.setText(" ");
        oefnungszeiten.setEditable(false);
        oefnungszeiten.setOpaque(false);
        Bewertungen.setText(" ");
        Bewertungen.setEditable(false);
        Bewertungen.setOpaque(false);
        adresse.setText(" ");
        Sperator1.setText(" ");
        Website.setText(" ");
        System.out.println(clubName.getText());

        setLayout(new BorderLayout());

        JButton backButton = new JButton("← Zurück");
        backButton.addActionListener(e -> showClubSelection());
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(backButton);
        add(topBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        JButton addRatingButton = new JButton("Bewertung hinzufügen");
        addRatingButton.addActionListener(e -> addRatingDialog());
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.add(addRatingButton);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void setNavigation(JSplitPane splitPane, Component clubOverview) {
        this.splitPane = splitPane;
        this.clubOverview = clubOverview;
    }

    // Zurück-Button Logik
    private void showClubSelection() {
        if (splitPane != null && clubOverview != null) {
            int divider = splitPane.getDividerLocation();
            splitPane.setLeftComponent(clubOverview);
            splitPane.setDividerLocation(divider);
            splitPane.revalidate();
            splitPane.repaint();
        }
    }

    private void addRatingDialog() {
        if (currentWaypoint == null) {
            return;
        }

        Layer8 currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Bitte zuerst anmelden, um zu bewerten.");
            return;
        }
        String author = currentUser.getusername();


        JComboBox<Integer> starsBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        starsBox.setSelectedItem(5);
        JTextArea textArea = new JTextArea(4, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        JPanel starsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        starsRow.add(new JLabel("Sterne (1-5):"));
        starsRow.add(starsBox);
        inputPanel.add(starsRow);
        inputPanel.add(new JLabel("Bewertung (optional):"));
        inputPanel.add(new JScrollPane(textArea));

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Bewertung für " + currentWaypoint.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        int stars = (Integer) starsBox.getSelectedItem();
        String text = textArea.getText().trim();
        currentWaypoint.addRating(author, stars, text);
        Bewertungen.setText(bewertungenFormat(currentWaypoint.getBewertungen()));
        revalidate();
        repaint();
    }

    private String oefnungszeitenFormat(String s){
        String[] tage = s.split(";\\s*");
        StringBuilder sb = new StringBuilder();
        for (String tag : tage) {
            sb.append("🕑 ").append(tag.trim()).append("\n");
        }
        return sb.toString().trim();
    }


    private String bewertungenFormat(String s){
        List<Rating> bewertungen = RatingManager.parseRatings(s);
        if (bewertungen.isEmpty()) {
            return "Noch keine Bewertungen vorhanden";
        }
        StringBuilder sb = new StringBuilder();
        for (Rating bewertung : bewertungen) {
            sb.append("👤 ").append(bewertung.getAuthor()).append("\n");
            sb.append(starsToString(bewertung.getStars())).append("\n");
            if (!bewertung.getText().isEmpty()) {
                sb.append("\n").append(bewertung.getText()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private String starsToString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= stars ? "★" : "☆");
        }
        return sb.toString();
    }

    public void zeigeWaypoint(infoWaypoint waypoint) {
        currentWaypoint = waypoint;
        System.out.println("Informationen werden angeziegt");
        if(beschreibung == null) {
            beschreibung = waypoint.getBeschreibung();
        }if(beschreibung == null){
            System.out.println("Beschreibung fehlt");
        }
        Website.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Website.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(beschreibung[2]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        beschreibung = waypoint.getBeschreibung();
        System.out.println("Anzeigen der Informationen");
        System.out.println(waypoint.getBeschreibung());
        clubName.setText(waypoint.getName());
        adresse.setText("📍"+beschreibung[0]);
        oefnungszeiten.setText(oefnungszeitenFormat(beschreibung[1]).replace(";", "\n"));
        Website.setText("🌐"+beschreibung[2].replace("http://", "").replace("https://", "").replace("/", ""));
        Bewertungen.setText(bewertungenFormat(waypoint.getBewertungen()));
        revalidate();
        repaint();
    }
}
