package Login.forms;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginOptionScreen loginOptionScreen = new LoginOptionScreen();
            loginOptionScreen.setVisible(true);
            ClubDatabase clubdatabase = new ClubDatabase(51.0, 6.3, 51.4, 6.6);
        });
    }
}