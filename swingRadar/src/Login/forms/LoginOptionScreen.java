package Login.forms;

import javax.swing.*;

public class LoginOptionScreen extends JFrame{
    private JPanel panel1;
    private JComboBox comboBox1;
    private JButton fortfahrenButton;
    private String loginOption;


    public LoginOptionScreen() {
        setTitle("Anmelden");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);

        fortfahrenButton.addActionListener(e -> {
            loginOption = (String) comboBox1.getSelectedItem();
            System.out.println("Login Option: " + loginOption);
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
            dispose();
        });
    }
}
