package Login.forms;

import javax.swing.*;

public class LoginScreen extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JCheckBox angemeldetBleibenCheckBox;
    private JButton anmeldenButton;
    private JButton regestrierenButton;
    private JLabel incorrectPassword;
    private JLabel Userdoesnotexist;
    private boolean angemeldetBleiben = false;
    private String username;
    private String password;
    private String role;

    public LoginScreen(String r) {
        role = r;
        setTitle("Anmelden");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);

        anmeldenButton.addActionListener(e -> {
            username = textField1.getText();
            password = new String(passwordField1.getPassword());
            angemeldetBleiben = angemeldetBleibenCheckBox.isSelected();
            // Reset both error labels before every login attempt.
            Userdoesnotexist.setText("");
            incorrectPassword.setText("");

            // Look the user up in the central user management (loaded from JSON).
            Layer8 user = UserManager.getInstance().findUserByName(username);
            if (user == null) {
                Userdoesnotexist.setText("Benutzer existiert nicht");
                return;
            }
            if (password.isEmpty() || !user.checkPassword(password)) {
                incorrectPassword.setText("Falsches Passwort");
                return;
            }

            // Remember the logged-in user so it can be used later, e.g. as the
            // author of a rating.
            UserManager.getInstance().setCurrentUser(user);

            System.out.println("Benutzername: " + username);
            System.out.println("Angemeldet bleiben: " + angemeldetBleiben);
            ClubMap mapframe = new ClubMap();
            dispose();
        });
        regestrierenButton.addActionListener(e -> {
            username = textField1.getText();
            password = new String(passwordField1.getPassword());
            angemeldetBleiben = angemeldetBleibenCheckBox.isSelected();
            Userdoesnotexist.setText("");
            incorrectPassword.setText("");

            if (password.isEmpty()) {
                incorrectPassword.setText("Gib ein Passwort an!");
                return;
            }

            // registerUser() adds the user, writes users.json, and returns null
            // if the name is already taken.
            Layer8 user = UserManager.getInstance().registerUser(username, password, angemeldetBleiben, role);
            if (user == null) {
                Userdoesnotexist.setText("Benutzer existiert bereits");
                return;
            }

            System.out.println("Benutzer registriert: " + username);
            System.out.println("Angemeldet bleiben: " + angemeldetBleiben);
        });
    }
}
