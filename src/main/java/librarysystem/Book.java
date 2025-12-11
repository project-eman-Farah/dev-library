package librarysystem;

import java.time.LocalDate;

/**
 * Represents a book in the library system.
 *
 * <p>This class extends {@link Media} and adds book-specific attributes such as:</p>
 * <ul>
 *     <li>Author name</li>
 *     <li>ISBN identifier</li>
 * </ul>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Store all book-related metadata</li>
 *     <li>Provide constructors for both new and file-loaded books</li>
 *     <li>Implement the book-specific fine rule (10 NIS per overdue day)</li>
 *     <li>Expose helper methods for borrowing, returning, and displaying book info</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Fine calculation is done through {@link Media#getOverdueFineAmount()} for polymorphism</li>
 *     <li>Borrow/return logic reused from Media to avoid duplication</li>
 *     <li>ISBN acts as the unique identifier for referencing and stock tracking</li>
 * </ul>
 */
public class Book extends Media {

    /** Name of the author. */
    private String author;

    /** Unique ISBN identifier for the book. */
    private String isbn;

    /**
     * Creates a new available book.
     *
     * @param title  book title
     * @param author book author
     * @param isbn   unique identifier
     */
    public Book(String title, String author, String isbn) {
        super(title);
        this.author = author;
        this.isbn = isbn;
    }

    /**
     * Constructor used when loading book data from the storage file.
     *
     * @param title     book title
     * @param author    book author
     * @param isbn      identifier
     * @param available availability status
     * @param dueDate   due date (or null)
     */
    public Book(String title, String author, String isbn, boolean available, LocalDate dueDate) {
        super(title);
        this.author = author;
        this.isbn = isbn;
        this.available = available;
        this.dueDate = dueDate;
    }

    /** @return book author */
    public String getAuthor() {
        return author;
    }

    /** @return unique ISBN identifier */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Borrows the book with a given due date and username.
     *
     * @param due      due date
     * @param username borrower
     */
    public void borrow(LocalDate due, String username) {
        super.borrow(due, username);
    }

    /**
     * Returns the book (sets available, clears dueDate and borrower).
     */
    public void returnBook() {
        super.returnItem();
    }

    /**
     * Fine rule: Books → 10 NIS per late day.
     *
     * @return fixed daily fine
     */
    @Override
    public int getOverdueFineAmount() {
        return 10;
    }

    /**

     * Sets the borrower (used when loading from file).
     *
     * @param username borrower username
     */
    public void setBorrowedBy(String username) {
        this.borrowedBy = username;
    }

    /**
     * Textual representation for file storage and on-screen display.
     */
    @Override
    public String toString() {
        if (available) {
            return title + " by " + author + " (ISBN: " + isbn + ") [Available]";
        }

        return title
                + " by " + author
                + " (ISBN: " + isbn + ") "
                + "[Borrowed by: " + borrowedBy
                + ", due: " + dueDate + "]";
    }
}
