package librarysystem;

/**
 * EmailService defines a generic contract for sending email notifications.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Provide a unified method for sending emails across the system</li>
 *     <li>Allow different implementations (real SMTP, console, mock testing)</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Return type is boolean to indicate success or failure</li>
 *     <li>Method signature is intentionally simple to keep implementations flexible</li>
 *     <li>Supports dependency injection in ReminderService and Main</li>
 * </ul>
 */
public interface EmailService {
    boolean send(String to, String subject, String body);
}

    /**
     * Sends an email message to a target user.
     *
     * @param to      recipient email address or username
     * @param subject subject line of the email
     * @param body    body content of the message
     * @return true if the message was sent successfully, false otherwise
     */
  