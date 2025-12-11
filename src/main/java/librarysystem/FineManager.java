package librarysystem;

import java.util.HashMap;
import java.util.Map;

public class FineManager {

    private final Map<String, Integer> fines = new HashMap<>();

    public void addFine(String user, int amount) {
        if (amount < 0) return; // ignore negatives
        fines.put(user, fines.getOrDefault(user, 0) + amount);
    }

    public int getFine(String user) {
        return fines.getOrDefault(user, 0);
    }

    public void payFine(String user, int amount) {
        if (amount < 0) return;

        int current = getFine(user);
        current -= amount;

        if (current < 0) current = 0;

        fines.put(user, current);
    }

    public boolean hasOutstandingFine(String user) {
        return getFine(user) > 0;
    }
}
