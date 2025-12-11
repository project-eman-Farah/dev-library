package librarysystem;

/**
 * InventoryItem represents a single media item (Book, CD, etc.)
 * inside the library's inventory, along with the number of total
 * and currently available copies.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Track how many copies of a media item exist in the library</li>
 *     <li>Track how many copies are currently available for borrowing</li>
 *     <li>Reduce available copies when a user borrows an item</li>
 *     <li>Increase available copies when a user returns an item</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>The Media object is stored as final because the item type never changes.</li>
 *     <li>Borrowing is prevented if no copies are available, ensuring data consistency.</li>
 *     <li>Available copies never exceed total copies.</li>
 *     <li>This class is intentionally simple and focused → Single Responsibility Principle.</li>
 * </ul>
 *
 * @author Team Library
 * @version 1.0
 */
public class InventoryItem {

    /** The media item (e.g., Book, CD) stored in inventory */
    private final Media media;

    /** Total number of copies the library owns */
    private int totalCopies;

    /** Number of copies currently available to borrow */
    private int availableCopies;

    /**
     * Creates an inventory item for a given media with a fixed number of copies.
     *
     * @param media  the media object (Book, CD, etc.)
     * @param copies total number of copies available at initialization
     */
    public InventoryItem(Media media, int copies) {
        this.media = media;
        this.totalCopies = copies;
        this.availableCopies = copies;
    }

    /**
     * @return the media object stored in this inventory item
     */
    public Media getMedia() {
        return media;
    }

    /**
     * @return total number of copies owned by the library
     */
    public int getTotalCopies() {
        return totalCopies;
    }

    /**
     * @return how many copies are currently available for borrowing
     */
    public int getAvailableCopies() {
        return availableCopies;
    }

    /**
     * Decreases the number of available copies when a user borrows this item.
     *
     * @throws IllegalStateException if no copies are available to borrow
     */
    public void borrowCopy() {
        if (availableCopies <= 0)
            throw new IllegalStateException("No copies available for this item.");

        availableCopies--;
    }

    /**
     * Increases the number of available copies when a user returns the item.
     * Ensures available copies never exceed total copies.
     */
    public void returnCopy() {
        if (availableCopies < totalCopies)
            availableCopies++;
    }
}
