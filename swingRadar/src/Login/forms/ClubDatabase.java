package Login.forms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    private static final String GOOGLE_API_KEY = "AIzaSyCB1u0DkWFBy7Y56oQRKnyb5KNPpQzlx_g";
    private static final String HERE_API_KEY = "LBC9Rmlvvs5QorKjbbRse9K8G8Wcwvx2kEe8zdVjCHc";
    
    /* APIs aktivieren oder deaktivieren
       Für Tests bitte nur OSM aktivieren
    */
    private static final boolean USE_HERE_API = false;
    private static final boolean USE_GOOGLE_API = false;
    private static final boolean USE_OSM_API = true;

    public ClubDatabase(double minLat, double minLon, double maxLat, double maxLon){
        if (USE_HERE_API) {
            System.out.println("Here API");
            HereApiAufruf(minLat, minLon, maxLat, maxLon);
        } else {
            System.out.println("HERE API deaktiviert");
        }
        
        if (USE_GOOGLE_API) {
            System.out.println("Google Places API");
            GoogleApiAufruf(minLat, minLon, maxLat, maxLon);
        } else {
            System.out.println("Google API deaktiviert");
        }
        
        if (USE_OSM_API) {
            System.out.println("OSM");
            OSMAufruf(minLat, minLon, maxLat, maxLon);
        } else {
            System.out.println("OSM deaktiviert");
        }

        loeschenVonDuplikaten();
        System.out.println("Anzahl an Clubs: " + clubs.size() + " Clubs");
    }
    
    private void loeschenVonDuplikaten() {
        ArrayList<clubinfos> deduplicated = new ArrayList<>();
        ArrayList<String> seenNames = new ArrayList<>();
        int duplicateCount = 0;
        
        // Process in order - Google/HERE results have priority since they're loaded first
        for (clubinfos club : clubs) {
            String normalizedName = normalizeName(club.getClubinfo().getName());
            if (!seenNames.contains(normalizedName)) {
                deduplicated.add(club);
                seenNames.add(normalizedName);
            } else {
                duplicateCount++;
                System.out.println("Kopie: " + club.getClubinfo().getName());
            }
        }
        
        System.out.println("Dopplungen: " + duplicateCount);
        clubs = deduplicated;
    }
    
    private String normalizeName(String name) {
        if (name == null) return "";
        return name.toLowerCase().trim().replaceAll("[^a-z0-9]", "");
    }

    private boolean HereApiAufruf(double minLat, double minLon, double maxLat, double maxLon) {
        try {
            String urlString = "https://discover.search.hereapi.com/v1/discover" +
                    "?at=" + ((minLat + maxLat) / 2) + "," + ((minLon + maxLon) / 2) +
                    "&q=nightclub" +
                    "&in=bbox:" + minLat + "," + minLon + "," + maxLat + "," + maxLon +
                    "&apiKey=" + HERE_API_KEY +
                    "&limit=100";
            
            System.out.println("HERE API-URL: " + urlString);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            System.out.println("HERE API...");
            
            if (conn.getResponseCode() != 200) {
                System.out.println("HERE API HTTP error: " + conn.getResponseCode());
                return false;
            }
            
            System.out.println("HERE API Status: " + conn.getResponseCode());
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            
            System.out.println("HERE API geladen");
            HereAntwortSpeichern(response.toString());
            return !clubs.isEmpty();
            
        } catch (Exception e) {
            System.out.println("HERE API Fehler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //Nach mehreren gescheiterten Versuchen die Google api zu erreichen wurde diese Methode mit Cascade ai SWE-1.6 erstellt
    private void GoogleApiAufruf(double minLat, double minLon, double maxLat, double maxLon) {
        try {
            String location = ((minLat + maxLat) / 2) + "," + ((minLon + maxLon) / 2);
            String radius = String.valueOf(Anzeigeradius(minLat, minLon, maxLat, maxLon));
            
            String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=" + location +
                    "&radius=" + radius +
                    "&type=night_club" +
                    "&key=" + GOOGLE_API_KEY;
            
            System.out.println("Google API-URL: " + urlString);
            System.out.println("Bereich: " + minLat + "," + minLon + " bis " + maxLat + "," + maxLon);
            System.out.println("Radius: " + radius + " Meter");
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            System.out.println("Google API...");
            
            int responseCode = conn.getResponseCode();
            System.out.println("Google API Response Code: " + responseCode);
            
            if (responseCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                System.out.println("Google API Error Response: " + errorResponse.toString());
                throw new RuntimeException("Google API HTTP error code: " + responseCode);
            }
            
            System.out.println("Google API Antwort erhalten, Status: " + responseCode);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            
            String responseString = response.toString();
            System.out.println("Google API Rohdaten geladen, Länge: " + responseString.length());
            System.out.println("Erste 500 Zeichen der Antwort: " + responseString.substring(0, Math.min(500, responseString.length())));
            
            GoogleAntwortSpeichern(responseString);
            
        } catch (Exception e) {
            System.out.println("Fehler beim Laden von Google Places API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double Anzeigeradius(double minLat, double minLon, double maxLat, double maxLon) {
        double latDiff = maxLat - minLat;
        double lonDiff = maxLon - minLon;
        double radius = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111000 / 2;
        return Math.min(radius, 50000);
    }

    private void HereAntwortSpeichern(String jsonResponse) {
        try {
            
            String itemsKey = "\"items\":";
            int itemsStart = jsonResponse.indexOf(itemsKey);
            if (itemsStart == -1) {
                System.out.println("HERE API: Antwort ist Leer");
                return;
            }
            
            String itemsSection = jsonResponse.substring(itemsStart + itemsKey.length());
            int arrayEnd = findMatchingBracket(itemsSection, 0);
            if (arrayEnd == -1) {
                System.out.println("HERE API: Ungültiges Format");
                return;
            }
            
            String items = itemsSection.substring(0, arrayEnd + 1);
            
            int index = 0;
            int clubCount = 0;
            while (index < items.length()) {
                int itemStart = items.indexOf("{", index);
                if (itemStart == -1) break;
                
                int itemEnd = findMatchingBracket(items, itemStart);
                if (itemEnd == -1) break;
                
                String item = items.substring(itemStart, itemEnd + 1);
                
                String name = extractJsonValue(item, "title");
                String lat = extractJsonValue(item, "lat");
                String lon = extractJsonValue(item, "lon");
                String address = extractJsonValue(item, "address");
                String category = extractJsonValue(item, "foodType");
                
                if (name != null && lat != null && lon != null) {
                    double latitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lon);
                    
                    String[] beschreibung = new String[3];
                    beschreibung[0] = address != null ? address : "Adresse unbekannt";
                    beschreibung[1] = category != null ? category : "Kategorie unbekannt";
                    beschreibung[2] = "";
                    
                    clubs.add(new clubinfos(name, latitude, longitude, beschreibung, ""));
                    clubCount++;
                    //System.out.println("HERE API Club: " + name + " bei " + latitude + ", " + longitude);
                }
                
                index = itemEnd + 1;
            }
            
            System.out.println("HERE API: " + clubCount + " Clubs geladen");
            
        } catch (Exception e) {
            System.out.println("HERE API: Fehler beim Speichern der Clubs: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void GoogleAntwortSpeichern(String jsonResponse) {
        try {
            String status = extractJsonValue(jsonResponse, "status");
            System.out.println("Google API Status: " + status);
            
            if (status != null && !status.equals("OK")) {
                String errorMessage = extractJsonValue(jsonResponse, "error_message");
                System.out.println("Google API Fehler: " + errorMessage);
                return;
            }

            String resultsSection = extractSection(jsonResponse, "results");
            if (resultsSection == null) {
                System.out.println("Google API: Keine Antwort");
                return;
            }
            
            System.out.println("Google API Antwortlänge: " + resultsSection.length());
            
            int index = 0;
            int clubCount = 0;
            int totalAttempts = 0;
            while (index < resultsSection.length()) {
                int resultStart = resultsSection.indexOf("{", index);
                if (resultStart == -1) break;
                
                int resultEnd = findMatchingBracket(resultsSection, resultStart);
                if (resultEnd == -1) break;
                
                String result = resultsSection.substring(resultStart, resultEnd + 1);
                totalAttempts++;
                
                String name = extractJsonValue(result, "name");
                String vicinity = extractJsonValue(result, "vicinity");
                
                String geometrySection = extractSection(result, "geometry");
                String lat = null;
                String lon = null;
                if (geometrySection != null) {
                    String locationSection = extractSection(geometrySection, "location");
                    if (locationSection != null) {
                        lat = extractJsonValue(locationSection, "lat");
                        lon = extractJsonValue(locationSection, "lng");
                    }
                }
                
                System.out.println("Versuch " + totalAttempts + ": name=" + name + ", lat=" + lat + ", lon=" + lon);
                
                if (name != null && lat != null && lon != null) {
                    double latitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lon);
                    
                    String[] beschreibung = new String[3];
                    beschreibung[0] = vicinity != null ? vicinity : "Adresse unbekannt";
                    beschreibung[1] = "Öffnungszeiten unbekannt";
                    beschreibung[2] = "";
                    
                    clubs.add(new clubinfos(name, latitude, longitude, beschreibung, ""));
                    clubCount++;
                    //System.out.println("Google API Club: " + name + " bei " + latitude + ", " + longitude);
                } else {
                    System.out.println("Fehlende Daten: name=" + name + ", lat=" + lat + ", lon=" + lon);
                }
                
                index = resultEnd + 1;
            }
            
            System.out.println("Google API geladene Clubs: " + clubCount + " von " + totalAttempts);
            
        } catch (Exception e) {
            System.out.println("Google API: Fehler beim Speichern der Clubs: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void OSMAufruf(double minLat, double minLon, double maxLat, double maxLon) {
        try {
            String bbox = minLat + "," + minLon + "," + maxLat + "," + maxLon;
            String query = "[out:json][timeout:25];" +
                          "nwr[\"amenity\"~\"bar|nightclub|pub\"](" + bbox + ");" +
                          "out center;";
            
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String urlString = "https://overpass-api.de/api/interpreter?data=" + encodedQuery;
            
            System.out.println("OSM API-URL: " + urlString);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            System.out.println("OSM API...");

            //Folgende Verzweigung Cascade ai SWE-1.6 erstellt
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code: " + conn.getResponseCode());
            }
            
            System.out.println("OSM API Status: " + conn.getResponseCode());
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            
            System.out.println("OSM Daten geladen");
            OSMAntwortSpeichern(response.toString());
            
        } catch (Exception e) {
            System.out.println("OSM API Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void OSMAntwortSpeichern(String jsonResponse) {
        try {
            
            int elementsStart = jsonResponse.indexOf("\"elements\":");
            if (elementsStart == -1) {
                System.out.println("OSM API: Geladene Daten sind Leer");
                return;
            }
            
            String elementsSection = jsonResponse.substring(elementsStart + 11);
            int arrayEnd = findMatchingBracket(elementsSection, 0);
            if (arrayEnd == -1) {
                System.out.println("OSM API: Ungültiges Format");
                return;
            }
            
            String elements = elementsSection.substring(0, arrayEnd + 1);
            
            int index = 0;
            int clubCount = 0;
            while (index < elements.length()) {
                int elementStart = elements.indexOf("{", index);
                if (elementStart == -1) break;
                
                int elementEnd = findMatchingBracket(elements, elementStart);
                if (elementEnd == -1) break;
                
                String element = elements.substring(elementStart, elementEnd + 1);
                
                String tagsSection = extractTagsSection(element);
                
                String name = extractJsonValue(tagsSection, "name");
                String lat = extractJsonValue(element, "lat");
                String lon = extractJsonValue(element, "lon");
                String website = extractJsonValue(tagsSection, "website");
                String openingHours = extractJsonValue(tagsSection, "opening_hours");
                String addr = extractJsonValue(tagsSection, "addr:street");
                String addrCity = extractJsonValue(tagsSection, "addr:city");
                String addrHousenumber = extractJsonValue(tagsSection, "addr:housenumber");
                
                System.out.println("Element: " + name);
                System.out.println("  - addr:street: " + addr);
                System.out.println("  - addr:city: " + addrCity);
                System.out.println("  - addr:housenumber: " + addrHousenumber);
                System.out.println("  - opening_hours: " + openingHours);
                System.out.println("  - Raw element sample: " + element.substring(0, Math.min(200, element.length())));
                
                if (name != null && lat != null && lon != null) {
                    double latitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lon);
                    
                    String address = "";
                    if (addr != null) {
                        address = addr;
                        if (addrHousenumber != null) {
                            address += " " + addrHousenumber;
                        }
                        if (addrCity != null) {
                            address += ", " + addrCity;
                        }
                    }
                    
                    String[] beschreibung = new String[3];
                    beschreibung[0] = address.isEmpty() ? "Adresse unbekannt" : address;
                    beschreibung[1] = openingHours != null ? openingHours : "Öffnungszeiten unbekannt";
                    beschreibung[2] = website != null ? website : "";
                    
                    clubs.add(new clubinfos(name, latitude, longitude, beschreibung, ""));
                    clubCount++;
                    //System.out.println("OSM API Club geladen: " + name + " bei " + latitude + ", " + longitude);
                }
                
                index = elementEnd + 1;
            }
            
            System.out.println("OSM API insgesamt geladene Clubs: " + clubCount);
            
        } catch (Exception e) {
            System.out.println("OSM API Fehler beim speichern: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Diese Methode wurde von Cascade ai SWE-1.6 erstellt
    private String extractSection(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int valueStart = keyIndex + searchKey.length();
        valueStart = Leerzeichenloeschen(json, valueStart);

        // Skip the colon
        if (valueStart < json.length() && json.charAt(valueStart) == ':') {
            valueStart++;
            valueStart = Leerzeichenloeschen(json, valueStart);
        }

        if (valueStart >= json.length()) return null;

        char c = json.charAt(valueStart);
        if (c == '{' || c == '[') {
            int sectionEnd = findMatchingBracket(json, valueStart);
            if (sectionEnd == -1) return null;
            return json.substring(valueStart, sectionEnd + 1);
        }
        return null;
    }

    //Diese Methode wurde von Cascade ai SWE-1.6 erstellt
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int valueStart = keyIndex + searchKey.length();
        valueStart = Leerzeichenloeschen(json, valueStart);
        
        // Skip the colon
        if (valueStart < json.length() && json.charAt(valueStart) == ':') {
            valueStart++;
            valueStart = Leerzeichenloeschen(json, valueStart);
        }
        
        if (valueStart >= json.length()) return null;
        
        char c = json.charAt(valueStart);
        if (c == '"') {
            int valueEnd = json.indexOf("\"", valueStart + 1);
            if (valueEnd == -1) return null;
            return json.substring(valueStart + 1, valueEnd);
        } else {
            int valueEnd = valueStart;
            while (valueEnd < json.length() && 
                   (Character.isLetterOrDigit(json.charAt(valueEnd)) ||
                    json.charAt(valueEnd) == '.' || 
                    json.charAt(valueEnd) == '-' ||
                    json.charAt(valueEnd) == '+')) {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd);
        }
    }

    //Diese Methode wurde von Cascade ai SWE-1.6 erstellt
    private String extractTagsSection(String element) {
        int tagsStart = element.indexOf("\"tags\":");
        if (tagsStart == -1) return element;
        
        int valueStart = tagsStart + 7;
        valueStart = Leerzeichenloeschen(element, valueStart);
        
        if (valueStart >= element.length() || element.charAt(valueStart) != '{') {
            return element;
        }
        
        int tagsEnd = findMatchingBracket(element, valueStart);
        if (tagsEnd == -1) return element;
        
        return element.substring(valueStart, tagsEnd + 1);
    }

    private int Leerzeichenloeschen(String s, int index) {
        while (index < s.length() && Character.isWhitespace(s.charAt(index))) {
            index++;
        }
        return index;
    }

    //Diese Methode wurde von Cascade ai SWE-1.6 erstellt
    private int findMatchingBracket(String s, int start) {
        int bracketCount = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '[' || c == '{') bracketCount++;
            else if (c == ']' || c == '}') {
                bracketCount--;
                if (bracketCount == 0) return i;
            }
        }
        return -1;
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

