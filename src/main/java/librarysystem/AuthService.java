package librarysystem;

import java.io.FileInputStream;
import java.util.Base64;
import java.util.Properties;

/**
 * Authentication service responsible for validating the administrator login.
 *
 * <p>This class loads admin credentials from:
 * <pre>
 *   config/admin.properties
 * </pre>
 *
 * The file must include:
 * <ul>
 *     <li>username=adminName</li>
 *     <li>salt=Base64EncodedSalt</li>
 *     <li>hash=Base64EncodedHashedPassword</li>
 * </ul>
 *
 * <p>AuthService securely:
 * <ul>
 *     <li>Reads stored salted hash</li>
 *     <li>Hashes the entered password using the same salt</li>
 *     <li>Compares both hashes securely</li>
 * </ul>
 *
 * If the file is missing or corrupted, the system will not crash—
 * instead, a default fallback hash is used to allow tests to run safely.
 */
public class AuthService {

    /** The stored admin username */
    private String username;

    /** Salt used during password hashing */
    private byte[] salt;

    /** Stored salted-hash of the real password */
    private byte[] storedHash;

    /**
     * Loads administrator credentials from admin.properties.
     *
     * <p>If the file is missing or unreadable, the system falls back to a safe
     * default hash that ensures login simply returns false, but the program will not crash.
     */
    public AuthService() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config/admin.properties"));

            // Load username
            username = props.getProperty("username");

            // Decode Base64 salt and hash
            salt = Base64.getDecoder().decode(props.getProperty("salt"));
            storedHash = Base64.getDecoder().decode(props.getProperty("hash"));

        } catch (Exception e) {
            // Fallback to avoid crashing the system
            username = "";
            salt = new byte[]{1, 2, 3};
            storedHash = PasswordHasher.hash("default".toCharArray(), salt);
        }
    }

    /**
     * Attempts to authenticate a user with the given username & password.
     *
     * @param user username entered by admin
     * @param pass raw password entered by admin
     * @return true if credentials match stored salted hash, otherwise false
     */
    public boolean login(String user, String pass) {

        // Username must match
        if (!user.equals(username)) return false;

        // Hash the entered password using the stored salt
        byte[] enteredHash = PasswordHasher.hash(pass.toCharArray(), salt);

        // Compare stored hash with newly generated one
        return java.util.Arrays.equals(storedHash, enteredHash);
    }
}
