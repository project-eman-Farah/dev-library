package librarysystem;

import java.time.LocalDate;

/**
 * Abstract base class representing a general media item in the library.
 *
 * <p>This class defines the shared attributes and behaviors for all
 * borrowable media types such as {@link Book} and {@link CD}.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Store general media information (title, availability, borrower, due date)</li>
 *     <li>Provide core borrow/return behavior</li>
 *     <li>Define a contract for overdue fine calculation</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>The class is abstract to enforce implementation of fine calculation in subclasses</li>
 *     <li>Borrow/return logic is centralized here to avoid duplication</li>
 *     <li>Availability, due date, and borrower stored directly inside Media to support all media types</li>
 * </ul>
 */
public abstract class Media {

    /** The title of the media item. */
    protected String title;

    /** Whether the media is available for borrowing. */
    protected boolean available = true;

    /** The due date for returning the borrowed item (null when available). */
    protected LocalDate dueDate;

    /** The username of the borrower (null when item is available). */
    protected String borrowedBy;

    /**
     * Constructs a media item with the given title.
     *
     * @param title the title of the media
     */
    public Media(String title) {
        this.title = title;
    }

    /**
     * Marks this media as borrowed by the given user.
     *
     * @param dueDate  the due date for returning the item
     * @param username the user who borrowed the item
     */
    public void borrow(LocalDate dueDate, String username) {
        this.available = false;
        this.dueDate = dueDate;
        this.borrowedBy = username;
    }

    /**
     * Returns the item and resets its status.
     * Sets availability to true and clears borrower and due-date data.
     */
    public void returnItem() {
        this.available = true;
        this.dueDate = null;
        this.borrowedBy = null;
    }

    /** @return true if the item is available for borrowing */
    public boolean isAvailable() {
        return available;
    }

    /** @return the due date or null if the item is available */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** @return the title of the media */
    public String getTitle() {
        return title;
    }

    /** @return the username of the borrower, or null if item is not borrowed */
    public String getBorrowedBy() {
        return borrowedBy;
    }

    /**
     * Returns the fine amount charged per overdue day.
     * Each media type must specify its own fine policy.
     *
     * @return the fine amount per overdue day
     */
    public abstract int getOverdueFineAmount();
}
