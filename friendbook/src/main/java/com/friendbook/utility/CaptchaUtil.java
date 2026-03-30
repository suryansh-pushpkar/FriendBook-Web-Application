package com.friendbook.utility;

import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class CaptchaUtil {
    private static final String SECRET_KEY = "6LfxnY0sAAAAAF7Latj82GSEC8Z0IL7r9jJamYwV";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify?secret={secret}&response={response}";

    public static boolean verifyCaptcha(String token) {
        if (token == null || token.isEmpty()) return false;

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject(VERIFY_URL, null, Map.class, SECRET_KEY, token);

            if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
                return false;
            }

            Object scoreObj = response.get("score");
            if (scoreObj instanceof Number) {
                return ((Number) scoreObj).doubleValue() >= 0.5;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}