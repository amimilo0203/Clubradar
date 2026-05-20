package Login.forms;

public class Layer8 {
    public String username;
    private String password;
    public boolean angemeldetBleiben;
    public String role;

    public Layer8(String u, String p, boolean a, String r){
        username = u;
        password = Hash.erstelleHash(p);
        System.out.println("Passwort: " + password);
        angemeldetBleiben = a;
        role = r;
    }
    public boolean checkPassword(String password){
        return Hash.erstelleHash(password).equals(this.password);
    }
    public String getusername() { return username; }
    public String getpassword() { return password; }
    public boolean getangemeldetBleiben() { return angemeldetBleiben; }
    public String getrole() { return role; }
}
