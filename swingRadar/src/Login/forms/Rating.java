package Login.forms;

/**
 * Eine einzelne Bewertung
 *
 * Dort drin sind:
 *   - Autor:  Nutzername,
 *   - Sterne: Sternebewertung zwischen 1 bis 5,
 *   - Text:   optionaler Bewertungstext (kann leer sein)
 */
public class Rating {
    private final String author; // username of the rater
    private final int stars;     // 1..5
    private final String text;   // optional, may be empty

    public Rating(String author, int stars, String text) {
        this.author = author;
        this.stars = stars;
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }

    public String toStorageString() {
        return author + "∞" + stars + "∞" + text;
    }
}
