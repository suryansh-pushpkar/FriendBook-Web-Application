package com.friendbook.utility;

import com.friendbook.repository.UserRepository;
import java.util.concurrent.ThreadLocalRandom;

public class UsernameUtil {
    public static String generateUniqueUsername(String fullName, UserRepository userRepository) {
        String cleanName = fullName.replaceAll("[^a-zA-Z]", "");

        while (cleanName.length() < 5) {
            cleanName += "x";
        }
        String part1 = cleanName.substring(0, 1).toUpperCase() +
                cleanName.substring(1, 5).toLowerCase();
        String finalUsername;
        boolean isTaken;
        do {
            int randomSuffix = ThreadLocalRandom.current().nextInt(100, 1000);
            finalUsername = part1 + randomSuffix;
            isTaken = userRepository.findByUsername(finalUsername).isPresent();
        } while (isTaken);
        return finalUsername;
    }
}