package librarysystem;
/**
 * UserManager maintains a registry of users who have logged into the system.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Register new users</li>
 *     <li>Check whether a user exists</li>
 *     <li>Unregister users if they meet system requirements</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Uses a HashSet for fast lookups (O(1))</li>
 *     <li>A user cannot be removed if:
 *         <ul>
 *             <li>They have unpaid fines</li>
 *             <li>They have active borrowed items</li>
 *         </ul>
 *     </li>
 *     <li>Library and FineManager are passed as parameters to keep the class decoupled</li>
 * </ul>
 */


import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private final Set<String> users = new HashSet<>();

    public void registerUser(String username) {
        users.add(username);
    }

    public boolean isRegistered(String username) {
        return users.contains(username);
    }

    public boolean unregisterUser(String username, Library library, FineManager fm) {

        if (!users.contains(username)) return false;

        if (fm.hasOutstandingFine(username)) return false;

        if (library.hasActiveLoans(username)) return false;

        users.remove(username);
        return true;
    }
}

