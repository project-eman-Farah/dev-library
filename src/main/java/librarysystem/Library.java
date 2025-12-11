package librarysystem;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * The Library class represents the core service layer of the Library Management System.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Load and save library items (Books & CDs) from a persistent text file</li>
 *     <li>Manage stock levels for all media types</li>
 *     <li>Handle borrowing and returning operations</li>
 *     <li>Apply overdue fines and detect lost items</li>
 *     <li>Send automated email notifications to users</li>
 *     <li>Enforce restrictions such as unpaid fines or lost items</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Uses a lightweight text file for persistence</li>
 *     <li>Stock is stored separately from Media objects</li>
 *     <li>FineManager and EmailSender are injected to keep logic modular</li>
 * </ul>
 */
public class Library {

    private static final String FILE_PATH = "library.txt";

    private final List<Book> books = new ArrayList<>();
    private final List<CD> cds = new ArrayList<>();

    private final FineManager fineManager;
    private final EmailSender emailSender = new EmailSender();

    private final Map<String, Integer> bookStock = new HashMap<>();
    private final Map<String, Integer> cdStock = new HashMap<>();

    public Library(FineManager fineManager) {
        this.fineManager = fineManager;
        ensureFileExists();
        loadItemsFromFile();
    }

    /** Ensures that the data file exists. */
    private void ensureFileExists() {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) f.createNewFile();
        } catch (Exception ignored) {}
    }

    /** Loads books and CDs from library.txt */
    private void loadItemsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] p = line.split(",", -1);
                if (p.length < 8) continue;

                String type = p[0];
                String title = p[1];
                String creator = p[2];
                String id = p[3];

                int copies = Integer.parseInt(p[4]);
                boolean available = Boolean.parseBoolean(p[5]);
                String borrowedBy = p[6].equals("null") ? null : p[6];
                LocalDate due = p[7].equals("null") ? null : LocalDate.parse(p[7]);

                if (type.equals("BOOK")) {
                    Book b = new Book(title, creator, id, available, due);
                    b.setBorrowedBy(borrowedBy);
                    books.add(b);
                    bookStock.put(id, copies);

                } else if (type.equals("CD")) {
                    CD cd = new CD(title, creator, id, available, due);
                    cd.setBorrowedBy(borrowedBy);
                    cds.add(cd);
                    cdStock.put(id, copies);
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading file.");
        }
    }

    /** Writes the full current state of the library to library.txt */
    private void updateLibraryFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Book b : books) {
                bw.write(String.join(",",
                        "BOOK",
                        b.getTitle(),
                        b.getAuthor(),
                        b.getIsbn(),
                        String.valueOf(bookStock.get(b.getIsbn())),
                        String.valueOf(b.isAvailable()),
                        String.valueOf(b.getBorrowedBy()),
                        String.valueOf(b.getDueDate())
                ));
                bw.newLine();
            }

            for (CD cd : cds) {
                bw.write(String.join(",",
                        "CD",
                        cd.getTitle(),
                        cd.getArtist(),
                        cd.getId(),
                        String.valueOf(cdStock.get(cd.getId())),
                        String.valueOf(cd.isAvailable()),
                        String.valueOf(cd.getBorrowedBy()),
                        String.valueOf(cd.getDueDate())
                ));
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }

    /** Checks general borrowing restrictions for users. */
    private boolean blocked(String username) {
        if (fineManager.hasOutstandingFine(username)) {
            System.out.println("❌ Borrow blocked: unpaid fines.");
            return true;
        }

        if (hasLostItem(username)) {
            System.out.println("❌ Borrow blocked: lost item exists.");
            return true;
        }

        return false;
    }

    /** Checks whether user has a lost item (30+ days overdue). */
    boolean hasLostItem(String username) {

        LocalDate today = LocalDate.now();

        for (Book b : books) {
            if (!b.isAvailable() &&
                username.equals(b.getBorrowedBy()) &&
                b.getDueDate() != null &&
                today.isAfter(b.getDueDate().plusDays(30)))
                return true;
        }

        for (CD cd : cds) {
            if (!cd.isAvailable() &&
                username.equals(cd.getBorrowedBy()) &&
                cd.getDueDate() != null &&
                today.isAfter(cd.getDueDate().plusDays(30)))
                return true;
        }

        return false;
    }

    // ==========================================================
    // ADD BOOK — MAIN + OVERLOADS
    // ==========================================================

    public void addBook(Book b, int copies) {
        books.add(b);
        bookStock.put(b.getIsbn(), copies);
        updateLibraryFile();
    }

    public void addBook(Book b) {
        addBook(b, 1);
    }

    public void addBook(String title, String author, String isbn, int copies) {
        Book b = new Book(title, author, isbn);
        addBook(b, copies);
    }

    public void addBook(String title, String author, String isbn) {
        addBook(title, author, isbn, 1);
    }

    // ==========================================================
    // ADD CD — MAIN + OVERLOADS
    // ==========================================================

    public void addCD(CD cd, int copies) {
        cds.add(cd);
        cdStock.put(cd.getId(), copies);
        updateLibraryFile();
    }

    public void addCD(CD cd) {
        addCD(cd, 1);
    }

    public void addCD(String title, String artist, String id, int copies) {
        CD cd = new CD(title, artist, id);
        addCD(cd, copies);
    }

    public void addCD(String title, String artist, String id) {
        addCD(title, artist, id, 1);
    }

    // ==========================================================
    // BORROW / RETURN BOOK
    // ==========================================================

    public void borrowBook(String isbn, String username) {

        if (blocked(username)) return;

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {

                int left = bookStock.getOrDefault(isbn, 0);
                if (left <= 0) {
                    System.out.println("❌ No copies left.");
                    return;
                }

                LocalDate due = LocalDate.now().plusDays(28);
                b.borrow(due, username);

                bookStock.put(isbn, left - 1);
                updateLibraryFile();

                emailSender.sendEmail(username,
                        "Book Borrowed",
                        "You borrowed: " + b.getTitle() + "\nDue: " + due);

                System.out.println("✔ Book borrowed.");
                return;
            }
        }

        System.out.println("❌ Book not found.");
    }

    public void returnBook(String isbn, String username) {

        for (Book b : books) {

            if (b.getIsbn().equals(isbn) &&
                username.equals(b.getBorrowedBy())) {

                LocalDate today = LocalDate.now();

                if (b.getDueDate() != null &&
                    today.isAfter(b.getDueDate())) {

                    long lateDays = java.time.temporal.ChronoUnit.DAYS
                            .between(b.getDueDate(), today);

                    int fine = (int) lateDays * b.getOverdueFineAmount();
                    fineManager.addFine(username, fine);

                    emailSender.sendEmail(username,
                            "Late Book Returned",
                            "You returned the book late.\nFine added: " + fine + " NIS");
                }

                b.returnItem();
                bookStock.put(isbn, bookStock.get(isbn) + 1);
                updateLibraryFile();

                emailSender.sendEmail(username,
                        "Book Returned",
                        "Returned: " + b.getTitle());

                System.out.println("✔ Book returned.");
                return;
            }
        }

        System.out.println("❌ You did not borrow this book.");
    }

    // ==========================================================
    // BORROW / RETURN CD
    // ==========================================================

    public void borrowCD(String id, String username) {

        if (blocked(username)) return;

        for (CD cd : cds) {
            if (cd.getId().equals(id)) {

                int left = cdStock.getOrDefault(id, 0);
                if (left <= 0) {
                    System.out.println("❌ No copies left.");
                    return;
                }

                LocalDate due = LocalDate.now().plusDays(7);
                cd.borrow(due, username);

                cdStock.put(id, left - 1);
                updateLibraryFile();

                emailSender.sendEmail(username,
                        "CD Borrowed",
                        "You borrowed CD: " + cd.getTitle() + "\nDue: " + due);

                System.out.println("✔ CD borrowed.");
                return;
            }
        }

        System.out.println("❌ CD not found.");
    }

    public void returnCD(String id, String username) {

        for (CD cd : cds) {
            if (cd.getId().equals(id) &&
                username.equals(cd.getBorrowedBy())) {

                LocalDate today = LocalDate.now();

                if (cd.getDueDate() != null &&
                    today.isAfter(cd.getDueDate())) {

                    long lateDays = java.time.temporal.ChronoUnit.DAYS
                            .between(cd.getDueDate(), today);

                    int fine = (int) lateDays * cd.getOverdueFineAmount();
                    fineManager.addFine(username, fine);

                    emailSender.sendEmail(username,
                            "Late CD Returned",
                            "You returned the CD late.\nFine added: " + fine + " NIS");
                }

                cd.returnCD();
                cdStock.put(id, cdStock.get(id) + 1);
                updateLibraryFile();

                emailSender.sendEmail(username,
                        "CD Returned",
                        "Returned: " + cd.getTitle());

                System.out.println("✔ CD returned.");
                return;
            }
        }

        System.out.println("❌ You did not borrow this CD.");
    }

    // ==========================================================
    // OVERDUE & LOST
    // ==========================================================

    public int checkOverdueBooks(String username) {

        int count = 0;
        LocalDate today = LocalDate.now();

        for (Book b : books) {
            if (!b.isAvailable() &&
                username.equals(b.getBorrowedBy()) &&
                b.getDueDate() != null &&
                today.isAfter(b.getDueDate())) {

                fineManager.addFine(username, b.getOverdueFineAmount());
                count++;

                emailSender.sendEmail(username,
                        "Overdue Book",
                        "Your book '" + b.getTitle() + "' is overdue!");
            }
        }

        for (CD cd : cds) {
            if (!cd.isAvailable() &&
                username.equals(cd.getBorrowedBy()) &&
                cd.getDueDate() != null &&
                today.isAfter(cd.getDueDate())) {

                fineManager.addFine(username, cd.getOverdueFineAmount());
                count++;

                emailSender.sendEmail(username,
                        "Overdue CD",
                        "Your CD '" + cd.getTitle() + "' is overdue!");
            }
        }

        return count;
    }

    public void checkLostItems(String username) {

        LocalDate today = LocalDate.now();

        for (Book b : books) {
            if (!b.isAvailable() &&
                username.equals(b.getBorrowedBy()) &&
                b.getDueDate() != null &&
                today.isAfter(b.getDueDate().plusDays(30))) {

                fineManager.addFine(username, 60);

                emailSender.sendEmail(username,
                        "Lost Book",
                        "You lost the book '" + b.getTitle() + "'. Fine: 60 NIS");

                updateLibraryFile();
            }
        }

        for (CD cd : cds) {
            if (!cd.isAvailable() &&
                username.equals(cd.getBorrowedBy()) &&
                cd.getDueDate() != null &&
                today.isAfter(cd.getDueDate().plusDays(30))) {

                fineManager.addFine(username, 40);

                emailSender.sendEmail(username,
                        "Lost CD",
                        "You lost the CD '" + cd.getTitle() + "'. Fine: 40 NIS");

                updateLibraryFile();
            }
        }
    }

    // ==========================================================
    // DISPLAY
    // ==========================================================

    public void showBooks() {
        System.out.println("=== BOOKS ===");
        for (Book b : books) {
            System.out.println(b + " | Copies: " + bookStock.get(b.getIsbn()));
        }
    }

    public void showCDs() {
        System.out.println("=== CDs ===");
        for (CD cd : cds) {
            System.out.println(cd + " | Copies: " + cdStock.get(cd.getId()));
        }
    }

    // ==========================================================
    // SEARCH
    // ==========================================================

    public void searchBook(String keyword) {
        boolean found = false;
        keyword = keyword.toLowerCase();

        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword) ||
                b.getAuthor().toLowerCase().contains(keyword) ||
                b.getIsbn().equalsIgnoreCase(keyword)) {

                System.out.println("✔ Found: " + b);
                found = true;
            }
        }

        if (!found) System.out.println("❌ No books found.");
    }

    // ==========================================================
    // ACTIVE LOANS
    // ==========================================================

    public boolean hasActiveLoans(String username) {

        for (Book b : books)
            if (!b.isAvailable() && username.equals(b.getBorrowedBy()))
                return true;

        for (CD cd : cds)
            if (!cd.isAvailable() && username.equals(cd.getBorrowedBy()))
                return true;

        return false;
    }
}
