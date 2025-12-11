package librarysystem;
/**
 * The Main class serves as the entry point for the entire Library Management System.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Initialize all core system services (authentication, fines, emails, library, users)</li>
 *     <li>Provide a command-line menu for admin interaction</li>
 *     <li>Handle login flow using AuthService and SessionManager</li>
 *     <li>Route user actions to the correct components (Library, UserManager, FineManager)</li>
 *     <li>Ensure the system can run from automated tests by supporting injected InputStream</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Menu logic is separated from system logic to keep Library.java clean</li>
 *     <li>runMenu() takes InputStream to support unit testing (dependency injection)</li>
 *     <li>Uses SessionManager to maintain login state and enforce admin-only actions</li>
 *     <li>Services are initialized once at startup and reused throughout runtime</li>
 * </ul>
 */


import java.io.InputStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        runMenu(System.in);
    }

    public static void runMenu(InputStream inputStream) {

        AuthService auth = new AuthService();
        SessionManager session = new SessionManager();
        FineManager fineManager = new FineManager();
        EmailService emailService = new RealEmailService();
        ReminderService reminderService = new ReminderService(emailService);
        Library library = new Library(fineManager);
        UserManager userManager = new UserManager();

        Scanner sc = new Scanner(inputStream);

        while (true) {

            System.out.println("\n===== Library System =====");
            System.out.println("1. Admin Login");
            System.out.println("2. Exit");
            System.out.print("Choose option: ");

            String choice = readInput(sc);

            if (choice.equals("1")) {

                while (!session.isLoggedIn()) {
                    System.out.print("Enter username: ");
                    String username = readInput(sc);
                    System.out.print("Enter password: ");
                    String password = readInput(sc);

                    if (auth.login(username, password)) {
                        session.login(username);
                        userManager.registerUser(username);
                        System.out.println("Login successful.");
                    } else {
                        System.out.println("Invalid credentials.\n");
                    }
                }

                while (session.isLoggedIn()) {

                    String currentUser = session.getCurrentUser();

                    System.out.println("\n----- Admin Menu -----");
                    System.out.println("1. Add Book");
                    System.out.println("2. View Books");
                    System.out.println("3. Search Book");
                    System.out.println("4. Borrow Book");
                    System.out.println("5. Add CD");
                    System.out.println("6. View CDs");
                    System.out.println("7. B"
                    		+ ""
                    		+ "orrow CD");
                    System.out.println("8. Check Overdue");
                    System.out.println("9. Pay Fine");
                    System.out.println("10. Unregister User");
                    System.out.println("11. Logout");
                    System.out.print("Choose: ");

                    String adminChoice = readInput(sc);

                    switch (adminChoice) {

                        case "1":
                            System.out.print("Enter title: ");
                            String t = readInput(sc);
                            System.out.print("Enter author: ");
                            String a = readInput(sc);
                            System.out.print("Enter ISBN: ");
                            String isbn = readInput(sc);
                            System.out.print("Enter copies: ");
                            int c = Integer.parseInt(readInput(sc));
                            library.addBook(new Book(t, a, isbn), c);
                            break;

                        case "2":
                            library.showBooks();
                            break;

                        case "3":
                            System.out.print("Keyword: ");
                            library.searchBook(readInput(sc));
                            break;

                        case "4":
                            System.out.print("Enter ISBN: ");
                            library.borrowBook(readInput(sc), currentUser);
                            break;

                        case "5":
                            System.out.print("Enter CD title: ");
                            String cdT = readInput(sc);
                            System.out.print("Enter CD artist: ");
                            String cdA = readInput(sc);
                            System.out.print("Enter CD ID: ");
                            String cdID = readInput(sc);
                            System.out.print("Enter copies: ");
                            int cdc = Integer.parseInt(readInput(sc));
                            library.addCD(new CD(cdT, cdA, cdID), cdc);
                            break;

                        case "6":
                            library.showCDs();
                            break;

                        case "7":
                            System.out.print("Enter CD ID: ");
                            library.borrowCD(readInput(sc), currentUser);
                            break;

                        case "8":
                            int overdue = library.checkOverdueBooks(currentUser);
                            if (overdue > 0)
                                reminderService.sendOverdueReminder(currentUser, overdue);
                            break;

                        case "9":
                            int fine = fineManager.getFine(currentUser);
                            System.out.println("Current fine: " + fine);
                            if (fine > 0) {
                                System.out.print("Enter amount: ");
                                int amount = Integer.parseInt(readInput(sc));
                                fineManager.payFine(currentUser, amount);
                            }
                            break;

                        case "10":
                            System.out.print("Enter username to remove: ");
                            userManager.unregisterUser(readInput(sc), library, fineManager);
                            break;

                        case "11":
                            session.logout();
                            break;

                        default:
                            System.out.println("Invalid.");
                    }
                }
            } 
            else if (choice.equals("2")) {
                System.out.println("System exiting.");
                break;
            } 
            else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    private static String readInput(Scanner sc) {
        String input = sc.nextLine().trim();
        while (input.isEmpty()) input = sc.nextLine().trim();
        return input;
    }
}
