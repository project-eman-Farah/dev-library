package librarysystem;

import java.time.LocalDate;

/**
 * Represents a CD in the library system.
 *
 * <p>This class extends {@link Media} and adds CD-specific details such as:</p>
 * <ul>
 *     <li>Artist name</li>
 *     <li>Unique CD identifier</li>
 * </ul>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Store metadata for CD items</li>
 *     <li>Support file-loading and normal construction</li>
 *     <li>Provide the CD-specific overdue fine rule (20 NIS per day)</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Fine rule defined through {@link Media#getOverdueFineAmount()} for polymorphism</li>
 *     <li>ID used as the key for inventory and file storage</li>
 *     <li>Borrow/return behavior inherited from Media to avoid code duplication</li>
 * </ul>
 */
public class CD extends Media {

    /** CD artist name. */
    private final String artist;

    /** Unique identifier used for referencing this CD. */
    private final String id;

    /**
     * Creates a new available CD.
     *
     * @param title   CD title
     * @param artist  CD artist name
     * @param id      unique identifier
     */
    public CD(String title, String artist, String id) {
        super(title);
        this.artist = artist;
        this.id = id;
    }

    /**
     * Constructor used when loading CD data from file.
     *
     * @param title     CD title
     * @param artist    CD artist
     * @param id        unique identifier
     * @param available availability state
     * @param dueDate   due date (may be null)
     */
    public CD(String title, String artist, String id, boolean available, LocalDate dueDate) {
        super(title);
        this.artist = artist;
        this.id = id;
        this.available = available;
        this.dueDate = dueDate;
    }

    /** @return CD artist */
    public String getArtist() { 
        return artist; 
    }

    /** @return unique CD identifier */
    public String getId() { 
        return id; 
    }

    /**
     * Fine rule: CDs → 20 NIS per overdue day.
     *
     * @return daily fine amount
     */
    @Override
    public int getOverdueFineAmount() {
        return 20;
    }

    /**
     * Returns the CD (marks it available and clears borrow data).
     */
    public void returnCD() {
        super.returnItem();
    }

    /**
     * Sets the borrower when loading CD data from file.
     *
     * @param username username of borrower
     */
    public void setBorrowedBy(String username) {
        this.borrowedBy = username;
    }

    /**
     * Textual representation used for display and file writing.
     */
    @Override
    public String toString() {
        if (available) {
            return title + " (CD, Artist: " + artist + ", ID: " + id + ") [Available]";
        }

        return title
                + " (CD, Artist: " + artist + ", ID: " + id + ") "
                + "[Borrowed by: " + borrowedBy
                + ", due: " + dueDate + "]";
    }
}
