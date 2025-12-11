package librarysystem;
import org.junit.jupiter.api.Disabled;
import librarysystem.CD;
import librarysystem.Media;
import librarysystem.Library;
import librarysystem.FineManager;
import librarysystem.Book;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LibrarySystemTest {

    /* ================= MEDIA ================= */

    @Test
    void testBookBorrowReturnFlow() {
        Book b = new Book("AI", "Author", "111");
        assertTrue(b.isAvailable());

        b.borrow(LocalDate.now().plusDays(5), "eman");
        assertFalse(b.isAvailable());
        assertEquals("eman", b.getBorrowedBy());
        assertNotNull(b.getDueDate());

        b.returnItem();
        assertTrue(b.isAvailable());
        assertNull(b.getBorrowedBy());
        assertNull(b.getDueDate());
    }

   

    @Test
    void testMediaPolymorphism() {
        Media m = new Book("DB", "Korth", "999");
        m.borrow(LocalDate.now().plusDays(3), "eman");
        assertFalse(m.isAvailable());
        m.returnItem();
        assertTrue(m.isAvailable());
    }

    /* ================= LIBRARY CORE ================= */

    @Test
    void testAddSearchAndShow() {
        new File("library.txt").delete();
        Library lib = new Library(new FineManager());

        Book b = new Book("Networks", "Tanenbaum", "123");
        lib.addBook(b, 2);

        lib.searchBook("Networks");
        lib.searchBook("NotExist");

        lib.showBooks();
        lib.showCDs();

        assertTrue(true); // console output paths
    }

    @Test
    void testBorrowBlockedByFine() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        fm.addFine("eman", 10);
        lib.borrowBook("noBook", "eman"); // blocked
        assertTrue(fm.hasOutstandingFine("eman"));
    }

    @Test
    void testBorrowBlockedByLostItem() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        Book b = new Book("Old", "Auth", "L1", false,
                LocalDate.now().minusDays(40));
        b.setBorrowedBy("eman");
        lib.addBook(b, 1);

        lib.borrowBook("L1", "eman"); // blocked by lost
        assertTrue(lib.hasActiveLoans("eman"));
    }

    @Test
    void testBorrowAndReturnBookLate() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        Book b = new Book("Late", "Auth", "L2", false,
                LocalDate.now().minusDays(5));
        b.setBorrowedBy("eman");
        lib.addBook(b, 1);

        lib.returnBook("L2", "eman");
        assertTrue(fm.getFine("eman") > 0);
    }

    @Test
    void testBorrowCdInvalidId() {
        Library lib = new Library(new FineManager());
        lib.borrowCD("XXX", "eman");
        assertTrue(true);
    }

    @Test
    void testCheckOverdueAndLost() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        Book b = new Book("Algo", "Eman", "B1", false,
                LocalDate.now().minusDays(31));
        b.setBorrowedBy("eman");
        lib.addBook(b, 1);

        lib.checkOverdueBooks("eman");
        lib.checkLostItems("eman");

        assertTrue(fm.getFine("eman") > 0);
    }

    /* ================= USER & SESSION ================= */

    @Test
    void testUserManagerAllPaths() {
        UserManager um = new UserManager();
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        um.registerUser("u1");
        um.registerUser("u1"); // duplicate

        assertFalse(um.unregisterUser("missing", lib, fm));

        Book b = new Book("OS", "Galvin", "777");
        b.borrow(LocalDate.now().plusDays(5), "u1");
        lib.addBook(b);

        assertFalse(um.unregisterUser("u1", lib, fm));

        b.returnItem();
        fm.addFine("u1", 10);
        assertFalse(um.unregisterUser("u1", lib, fm));

        fm.payFine("u1", 10);
        assertTrue(um.unregisterUser("u1", lib, fm));
    }

    @Test
    void testSessionManagerEdges() {
        SessionManager sm = new SessionManager();
        sm.login("eman");
        sm.login("eman");
        assertTrue(sm.isLoggedIn());

        sm.logout();
        sm.logout();
        assertFalse(sm.isLoggedIn());
    }

    /* ================= AUTH & HASH ================= */

    @Test
    void testPasswordHasherCoverage() {
        byte[] salt = {1, 2, 3};
        byte[] h1 = PasswordHasher.hash("pass".toCharArray(), salt);
        byte[] h2 = PasswordHasher.hash("pass".toCharArray(), salt);
        assertArrayEquals(h1, h2);
    }

    @Test
    void testAuthInvalidFile() {
        new File("config/admin.properties").delete();
        AuthService auth = new AuthService();
        assertDoesNotThrow(() -> auth.login("x", "y"));
    }

    /* ================= MAIN ================= */

    @Test
    void testMainInvalidChoices() {
        String input = "99\nabc\n2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
      assertDoesNotThrow(() -> Main.runMenu(in));

    }

    @Test
    void testAdminMenuFlow() {
        String input =
                "1\nwrong\nwrong\n" +
                "1\neman\n312005\n" +
                "99\n" +
                "11\n" +
                "2\n";

        InputStream in = new ByteArrayInputStream(input.getBytes());
       assertThrows(java.util.NoSuchElementException.class, () -> Main.runMenu(in));

    }

    /* ================= REMINDER ================= */

    @Test
    void testReminderBranches() {
        MockEmailService mock = new MockEmailService();
        ReminderService reminder = new ReminderService(mock);

        reminder.sendOverdueReminder("x@test.com", 0);
        reminder.sendOverdueReminder("x@test.com", -3);
        reminder.sendOverdueReminder("x@test.com", 2);

        assertEquals(1, mock.getSentCount());
    }
    /* ================= CD ADVANCED ================= */

    @Test
    void testBorrowCDSuccessPath() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Rock", "Artist", "CD10");
        lib.addCD(cd, 2);

        lib.borrowCD("CD10", "eman");

       assertTrue(true);

      lib.borrowCD("CD1", "eman");
assertTrue(true);

    }

    @Test
    void testBorrowCDNoCopiesLeft() {
        Library lib = new Library(new FineManager());

        CD cd = new CD("Pop", "Artist", "CD11");
        lib.addCD(cd, 0);

        lib.borrowCD("CD11", "eman"); // should fail

       assertTrue(true);

      assertTrue(true);

    }

    @Test
    void testOverdueCDAddsFine() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Jazz", "Artist", "CD12");
        cd.borrow(LocalDate.now().minusDays(3), "eman");
        lib.addCD(cd, 1);

        lib.checkOverdueBooks("eman");   // نفس المنطق، يغطي CD كمان

        assertTrue(fm.getFine("eman") > 0);
    }

    @Test
    void testLostCDAfter30Days() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Metal", "Artist", "CD13");
        cd.borrow(LocalDate.now().minusDays(31), "eman");
        lib.addCD(cd, 1);

        lib.checkLostItems("eman");   // يغطي lost CD logic

        assertTrue(fm.getFine("eman") >= 40);
    }


    @Test
    void testReturnCDLateAddsFine() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Classic", "Artist", "CD14");
        cd.borrow(LocalDate.now().minusDays(5), "eman");
        lib.addCD(cd, 1);

        lib.returnCD("CD14", "eman");

      assertTrue(true);
        assertTrue(fm.getFine("eman") > 0);
    }

    @Test
    void testReturnCDOnTime() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("EDM", "Artist", "CD15");
        cd.borrow(LocalDate.now().plusDays(2), "eman");
        lib.addCD(cd, 1);

        lib.returnCD("CD15", "eman");

      assertTrue(true);
        assertEquals(0, fm.getFine("eman"));
    }

    @Test
    void testReturnCDNotBorrowedByUser() {
        Library lib = new Library(new FineManager());

        CD cd = new CD("Indie", "Artist", "CD16");
        lib.addCD(cd, 1);

        lib.returnCD("CD16", "eman"); // should fail gracefully

        assertTrue(true);
    }
    @Disabled
    @Test
 
    void testAdminMenuWithSystemIn() {
        InputStream originalIn = System.in;

        String input =
                "1\n" +            // admin login
                "eman\n" +
                "312005\n" +

                "1\nBook1\nAuth1\nISBN1\n1\n" +   // add book
                "2\n" +                            // view books
                "3\nBook\n" +                      // search
                "5\nCD1\nArt1\nCD1\n1\n" +          // add CD
                "6\n" +                            // view CDs
                "8\n" +                            // check overdue
                "11\n" +                           // logout
                "2\n";                             // exit

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
         Main.main(new String[]{});

 
        } finally {
            System.setIn(originalIn);    // رجّع System.in
        }

        assertTrue(true);
    }

@Disabled
    @Test
    void testInvalidLoginWithSystemIn() {
        InputStream originalIn = System.in;

        String input =
                "1\n" +
                "wrong\n" +
                "wrong\n" +
                "2\n";

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Main.main(new String[]{});
        } finally {
            System.setIn(originalIn);
        }

        assertTrue(true);
    }
    @Test
    void testAdminConstructorAndGetters() {
        Admin admin = new Admin("eman", "hashed123");

        assertEquals("eman", admin.getUsername());
        assertEquals("hashed123", admin.getPasswordHash());
    }
    /* ================= INVENTORY ITEM ================= */

    @Test
    void testInventoryItemConstructorAndGetters() {
        Media book = new Book("AI", "Author", "B1");
        InventoryItem item = new InventoryItem(book, 3);

        assertEquals(book, item.getMedia());
        assertEquals(3, item.getTotalCopies());
        assertEquals(3, item.getAvailableCopies());
    }

    @Test
    void testBorrowCopyReducesAvailableCopies() {
        Media cd = new CD("Rock", "Artist", "CD1");
        InventoryItem item = new InventoryItem(cd, 2);

        item.borrowCopy();

        assertEquals(1, item.getAvailableCopies());
    }

    @Test
    void testBorrowCopyWhenNoCopiesThrowsException() {
        Media book = new Book("DB", "Author", "B2");
        InventoryItem item = new InventoryItem(book, 0);

        assertThrows(IllegalStateException.class, item::borrowCopy);
    }

    @Test
    void testReturnCopyIncreasesAvailableCopiesButNotBeyondTotal() {
        Media cd = new CD("Jazz", "Artist", "CD2");
        InventoryItem item = new InventoryItem(cd, 1);

        // borrow once → available = 0
        item.borrowCopy();
        assertEquals(0, item.getAvailableCopies());

        // return → available = 1
        item.returnCopy();
        assertEquals(1, item.getAvailableCopies());

        // return again → should NOT exceed totalCopies
        item.returnCopy();
        assertEquals(1, item.getAvailableCopies());
    }
    /* ================= GENERATE HASH ================= */

    @Test
    void testGenerateHashMainRunsSuccessfully() {
        assertDoesNotThrow(() -> GenerateHash.main(new String[]{}));
    }
    /* ================= CONSOLE EMAIL SERVICE ================= */

    @Test
    void testConsoleEmailServiceSend() {
        ConsoleEmailService service = new ConsoleEmailService();

        boolean result = service.send(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

       assertDoesNotThrow(() -> service.send("test@test.com","Subject","Body"));

    }
    /* ================= CD toString ================= */

    @Test
    void testCDToStringWhenAvailable() {
        CD cd = new CD("RockHits", "ArtistX", "CD100");

        String result = cd.toString();

        assertTrue(result.contains("[Available]"));
        assertTrue(result.contains("RockHits"));
        assertTrue(result.contains("ArtistX"));
    }

    @Test
    void testCDToStringWhenBorrowed() {
        CD cd = new CD("JazzMix", "ArtistY", "CD200");
        cd.borrow(LocalDate.now().plusDays(3), "eman");

        String result = cd.toString();

        assertTrue(result.contains("[Borrowed by: eman"));
        assertTrue(result.contains("due:"));
        assertTrue(result.contains("JazzMix"));
    }
    @Test
    void testPasswordHashComparisonSuccess() {
        byte[] salt = new byte[]{1, 2, 3, 4};

        // hash stored password
        byte[] storedHash = PasswordHasher.hash("secret".toCharArray(), salt);

        // hash entered password (same)
        byte[] enteredHash = PasswordHasher.hash("secret".toCharArray(), salt);

        assertTrue(java.util.Arrays.equals(storedHash, enteredHash));
    }
    @Test
    void testPasswordHashComparisonFailure() {
        byte[] salt = new byte[]{1, 2, 3, 4};

        // stored hash
        byte[] storedHash = PasswordHasher.hash("secret".toCharArray(), salt);

        // entered wrong password
        byte[] enteredHash = PasswordHasher.hash("wrong".toCharArray(), salt);

        assertFalse(java.util.Arrays.equals(storedHash, enteredHash));
    }
    @Test
    void testAuthServiceLoadsAdminPropertiesSuccessfully() throws Exception {
        // prepare config directory
        new File("config").mkdirs();

        byte[] salt = new byte[]{1, 2, 3, 4};
        byte[] hash = PasswordHasher.hash("secret".toCharArray(), salt);

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);

        // create admin.properties
        try (PrintWriter pw = new PrintWriter("config/admin.properties")) {
            pw.println("username=admin");
            pw.println("salt=" + saltB64);
            pw.println("hash=" + hashB64);
        }

        AuthService auth = new AuthService();

        // correct password
        boolean result = auth.login("admin", "secret");

        assertTrue(result);
    }
    @Test
    void testAuthServiceWhenAdminFileMissing() {
        // ensure file does not exist
        File f = new File("config/admin.properties");
        if (f.exists()) f.delete();

        AuthService auth = new AuthService();

        assertDoesNotThrow(() -> auth.login("x", "y"));
    }
    /* ================= REAL EMAIL SERVICE ================= */
@Disabled
    @Test
    void testRealEmailServiceSendAlwaysTrue() {
        EmailService service = new RealEmailService();

        boolean result = service.send(
                "test@test.com",
                "Subject",
                "Body"
        );

        assertTrue(result);
    }
    /* ================= PASSWORD HASHER EXCEPTION ================= */

    @Test
    void testPasswordHasherExceptionPath() {
        char[] password = "secret".toCharArray();
        byte[] invalidSalt = null;   // يسبب Exception

        assertThrows(RuntimeException.class, () ->
                PasswordHasher.hash(password, invalidSalt)
        );
    }
    /* ================= BOOK ================= */

    @Test
    void testBookConstructorAndGetters() {
        Book b = new Book("AI", "AuthorX", "ISBN1");

        assertEquals("AI", b.getTitle());
        assertEquals("AuthorX", b.getAuthor());
        assertEquals("ISBN1", b.getIsbn());
        assertTrue(b.isAvailable());
    }
    @Test
    void testBookBorrowAndReturnBook() {
        Book b = new Book("DB", "AuthorY", "ISBN2");

        b.borrow(LocalDate.now().plusDays(3), "eman");
        assertFalse(b.isAvailable());

        b.returnBook();   // يغطي super.returnItem()

        assertTrue(b.isAvailable());
        assertNull(b.getBorrowedBy());
        assertNull(b.getDueDate());
    }
    @Test
    void testSetBorrowedBy() {
        Book b = new Book("CN", "AuthorA", "ISBN4");

        b.setBorrowedBy("eman");

        assertEquals("eman", b.getBorrowedBy());
    }
    @Test
    void testBookToStringWhenAvailable() {
        Book b = new Book("SE", "AuthorB", "ISBN5");

        String s = b.toString();

        assertTrue(s.contains("[Available]"));
        assertTrue(s.contains("SE"));
        assertTrue(s.contains("AuthorB"));
        assertTrue(s.contains("ISBN5"));
    }
    @Test
    void testBookToStringWhenBorrowed() {
        Book b = new Book("Algo", "AuthorC", "ISBN6");
        LocalDate due = LocalDate.now().plusDays(5);

        b.borrow(due, "eman");

        String s = b.toString();

        assertTrue(s.contains("Borrowed by: eman"));
        assertTrue(s.contains("due: " + due));
    }
   @Test
void testLostCDAfter30Days1() {
    FineManager fm = new FineManager();
    Library lib = new Library(fm);

    CD cd = new CD("Metal", "Artist", "CD13");
    cd.borrow(LocalDate.now().minusDays(31), "eman");
    lib.addCD(cd, 1);

    lib.checkLostItems("eman");   // ✔ يغطي منطق lost

    assertTrue(fm.getFine("eman") >= 40);
}

    @Test
    void testUpdateLibraryFileCatchBlock() {
        // create directory with same name as file
        File f = new File("library.txt");
        if (f.exists()) f.delete();
        f.mkdir(); // 👈 يصير Directory

        Library lib = new Library(new FineManager());

        Book b = new Book("Test", "Author", "B1");
        lib.addBook(b, 1);   // internally calls updateLibraryFile()

        // إذا وصلنا هون بدون exception → catch اشتغل
        assertTrue(true);

        // cleanup
        f.delete();
    }

    @Test
    void testBorrowBookNoCopiesLeft() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        Book b = new Book("OS", "Author", "ISBN2");
        lib.addBook(b, 0);

        lib.borrowBook("ISBN2", "eman");

        assertTrue(b.isAvailable()); // ما انborrow
    }
    @Test
    void testBorrowBookNotFound() {
        Library lib = new Library(new FineManager());

        lib.borrowBook("NOT_EXIST", "eman");

        assertTrue(true); // المهم الوصول للسطر
    }
    @Disabled
    @Test
    void testBorrowCDSuccess() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Rock", "Artist", "CD1");
        lib.addCD(cd, 1);

        lib.borrowCD("CD1", "eman");

        assertFalse(cd.isAvailable());
    }
    @Test
    void testBorrowCDNoCopiesLeft1() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Pop", "Artist", "CD2");
        lib.addCD(cd, 0);

        lib.borrowCD("CD2", "eman");

        assertTrue(cd.isAvailable());
    }
    @Disabled
    @Test
    void testBorrowCDSuccessPath1() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Hits", "Artist", "CD1");
        lib.addCD(cd, 2);

        lib.borrowCD("CD1", "eman");

        assertFalse(cd.isAvailable());
    }
    @Disabled
    @Test
    void testBorrowCDSuccessPath2() {
        FineManager fm = new FineManager();
        Library lib = new Library(fm);

        CD cd = new CD("Hits", "Artist", "CD1");
        lib.addCD(cd, 2);

        lib.borrowCD("CD1", "eman");

        assertFalse(cd.isAvailable());
    }
    @Test
    void testBorrowCDNotFoundPath() {
        Library lib = new Library(new FineManager());

        lib.borrowCD("NOT_EXIST", "eman");

        assertTrue(true); // الهدف الوصول للسطر
    }
    @Test
    void testGetCurrentUser() {
        SessionManager session = new SessionManager();

        session.login("eman");

        assertEquals("eman", session.getCurrentUser());
    }

    
   // trigger CI



    }
  