package Login.forms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class ClubDatabase {
    private File eingetrageneClubs = new File("/Users/milo/IdeaProjects/Clubradar/swingRadar/src/Login/forms/Clubs.txt");
    private static ClubDatabase clubdatabase;
    private static ArrayList<clubinfos> clubs = new ArrayList<>();



    public ClubDatabase(){
        System.out.println("Clubdatabase wird gelesen");
        try (Scanner myReader = new Scanner(eingetrageneClubs)) {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] clubdata = data.split("¡\\s*");
                String[] generalinfo = clubdata[0].split("¿\\s*");
                String[] beschreibung = clubdata[1].split("¿\\s*");
                //String[] bewertungen = clubdata[2].split("¿\\s*");
                clubs.add(new clubinfos(generalinfo[2], Double.parseDouble(generalinfo[0]), Double.parseDouble(generalinfo[1]), beschreibung, clubdata[2]));
                System.out.println(beschreibung);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    public void sortClubs() {
        //clubs.sort();
    }
    public static ArrayList<clubinfos> getClubs() {
        return clubs;
    }
    public static clubinfos getClub(int index) {
        return clubs.get(index);
    }
}

