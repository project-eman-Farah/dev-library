package librarysystem;

import java.util.ArrayList;
import java.util.List;

public class MockEmailService implements EmailService {

    private int sentCount = 0;
    private List<String> bodies = new ArrayList<>();

    @Override
    public boolean send(String to, String subject, String body) {
        sentCount++;
        bodies.add(body);
        return true;
    }

    public int getSentCount() {
        return sentCount;
    }

    public List<String> getBodies() {
        return bodies;
    }
}
