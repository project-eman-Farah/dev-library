package librarysystem;

import java.util.Base64;

/**
 * GenerateHash is a utility tool used during system setup to generate
 * Base64-encoded salts and hashed passwords for secure storage inside
 * configuration files such as <code>admin.properties</code>.
 *
 * <p><b>Purpose:</b></p>
 * This class is not used during system runtime. It is used only once:
 * to generate a secure hash for the admin's password before deployment.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Allow developers to enter a username and plain-text password.</li>
 *     <li>Generate a salt (fixed or random).</li>
 *     <li>Produce a Base64-encoded password hash using {@link PasswordHasher}.</li>
 *     <li>Print the values in a ready-to-copy format for admin.properties.</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Salt length matches system tests (4 bytes).</li>
 *     <li>Uses Base64 encoding because properties files cannot store raw bytes.</li>
 *     <li>Password hashing is delegated to PasswordHasher → Separation of concerns.</li>
 *     <li>Class contains only a main method because it serves as a temporary setup tool.</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>
 *   username = admin
 *   password = mySecret123
 *
 *   (Run this class → output for admin.properties appears)
 * </pre>
 *
 * @author Team Library
 * @version 1.0
 */
public class GenerateHash {

    /**
     * Generates Base64-encoded salt and hash for a given username/password.
     * Prints the result in a format compatible with admin.properties.
     *
     * @param args not used
     */
    public static void main(String[] args) {

        // TODO: Developer should enter username + password here before running the tool.
        String username = "";
        String password = "";   

        // Salt used in the tests (4 bytes)
        byte[] salt = new byte[]{1, 2, 3, 4};

        // Hash the password using PBKDF2 (PasswordHasher class)
        byte[] hash = PasswordHasher.hash(password.toCharArray(), salt);

        // Convert raw bytes to Base64 for safe storage
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        // Print the results in a ready-to-use format
        System.out.println("Use this inside admin.properties:");
        System.out.println("username=" + username);
        System.out.println("salt=" + saltBase64);
        System.out.println("hash=" + hashBase64);
    }
}
