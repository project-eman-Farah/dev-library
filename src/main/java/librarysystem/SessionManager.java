package librarysystem;
/**
 * SessionManager handles the login state of the currently active user.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Store the username of the logged-in user</li>
 *     <li>Provide methods to log in, log out, and check session status</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Simple string-based session to keep system lightweight</li>
 *     <li>No multi-user session support since only admin uses the system</li>
 * </ul>
 */

public class SessionManager {

    private String currentUser = null;

    public void login(String user) {
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getCurrentUser() {
        return currentUser;
    }
}
