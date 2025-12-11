package librarysystem;

public class ConsoleEmailService implements EmailService {

	@Override
	public boolean send(String to, String subject, String body) {
	    System.out.println("\n===== EMAIL SENT (Console) =====");
	    System.out.println("To: " + to);
	    System.out.println("Subject: " + subject);
	    System.out.println("Body: " + body);
	    System.out.println("================================\n");

	    return true; // console always successful
	}

}
