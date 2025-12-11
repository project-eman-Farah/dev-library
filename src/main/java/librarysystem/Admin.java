package librarysystem;

/**
 * Represents the administrator account of the Library System.
 *
 * <p>This class holds the essential authentication data for the admin:
 * <ul>
 *     <li>username — the unique admin login name</li>
 *     <li>passwordHash — the hashed password stored securely</li>
 * </ul>
 *
 * <p>The Admin object is used by {@link AuthService} during login validation.
 */
public class Admin {

    /** The username of the admin */
    private final String username;

    /** The hashed password stored in admin.properties */
    private final String passwordHash;

    /**
     * Constructs an Admin object.
     *
     * @param username     the admin's login name
     * @param passwordHash the hashed password associated with this account
     */
    public Admin(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * @return the administrator username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the hashed password stored for authentication
     */
    public String getPasswordHash() {
        return passwordHash;
    }
}
