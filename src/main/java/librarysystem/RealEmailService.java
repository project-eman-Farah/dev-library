package librarysystem;

public class RealEmailService implements EmailService {

    private final EmailSender sender = new EmailSender();

   
    @Override
    public boolean send(String to, String subject, String body) {
        // existing email logic
        return true;
    }

}
