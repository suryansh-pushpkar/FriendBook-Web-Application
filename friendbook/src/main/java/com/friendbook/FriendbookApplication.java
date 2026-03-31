package com.friendbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class FriendbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendbookApplication.class, args);

        System.out.println("Hello FriendBook ");
    }

}
