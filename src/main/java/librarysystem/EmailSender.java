package librarysystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailSender {

    private final String username;
    private final String password;
    private final String smtpHost;
    private final String smtpPort;

    public EmailSender() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config/email.properties"));
        } catch (IOException e) {
            throw new RuntimeException("❌ Cannot load email.properties", e);
        }

        this.username = props.getProperty("email");
        this.password = props.getProperty("appPassword");
        this.smtpHost = props.getProperty("smtpHost");
        this.smtpPort = props.getProperty("smtpPort");
    }

    /**
     * Sends an email using SMTP settings.
     *
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);

            System.out.println("📧 Email sent to: " + to);
            return true;

        } catch (Exception e) {
            System.out.println("❌ Email error: " + e.getMessage());
            return false;
        }
    }
}
