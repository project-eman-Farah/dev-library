package librarysystem;

/**
 * ReminderService is responsible for sending email reminders related
 * to overdue library items. The class uses dependency injection to allow
 * different email delivery mechanisms (real SMTP, console output, mock tests).
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Detect when reminders should be sent</li>
 *     <li>Construct reminder messages</li>
 *     <li>Send emails using the configured EmailService implementation</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b></p>
 * <ul>
 *     <li>Uses EmailService interface → allows easy testing and injection</li>
 *     <li>Does not interact directly with Library or fine logic → keeps concerns separated</li>
 *     <li>Reminder format kept simple to avoid UI complexity</li>
 * </ul>
 */
public class ReminderService {

    /** Email service used to send reminder notifications */
    private final EmailService emailService;

    /**
     * Creates a ReminderService using a specific EmailService implementation.
     *
     * @param emailService the EmailService used to send messages
     */
    public ReminderService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sends an overdue reminder if the user has overdue items.
     *
     * @param email  recipient's email address or username
     * @param count  number of overdue items
     */
    public void sendOverdueReminder(String email, int count) {
        if (count <= 0) return; // no overdue items → no reminder needed

        String msg = "You have " + count + " overdue book(s).";
        emailService.send(email, "Overdue Reminder", msg);
    }
}
