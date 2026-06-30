package Login.forms;

public class Layer8 {
    public String username;
    private String password;
    public boolean angemeldetBleiben;
    public String role;

    public Layer8(String u, String p, boolean a, String r){
        username = u;
        password = Hash.erstelleHash(p);
        angemeldetBleiben = a;
        role = r;
    }

    // Rebuilded user von bereits vorhandener Daten (users.json)
    public static Layer8 fromStoredData(String username, String passwordHash, boolean stayLoggedIn, String role) {
        Layer8 user = new Layer8(username, "", stayLoggedIn, role);
        user.password = passwordHash;
        return user;
    }

    public boolean checkPassword(String password){
        return Hash.erstelleHash(password).equals(this.password);
    }
    public String getusername() { return username; }
    public String getpassword() { return password; }
    public boolean getangemeldetBleiben() { return angemeldetBleiben; }
    public String getrole() { return role; }
}
