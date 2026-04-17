package com.livepoll.config;

import com.livepoll.entity.*;
import com.livepoll.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PollRepository pollRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed admin user
        User admin = new User(
            "Admin User",
            "admin@livepoll.com",
            passwordEncoder.encode("admin123"),
            "LOCAL",
            Set.of(Role.ADMIN, Role.USER)
        );
        userRepository.save(admin);

        // Seed regular user
        User user = new User(
            "John Doe",
            "user@livepoll.com",
            passwordEncoder.encode("user123"),
            "LOCAL",
            Set.of(Role.USER)
        );
        userRepository.save(user);

        // Seed sample polls
        Poll poll1 = new Poll();
        poll1.setQuestion("What is your favourite programming language?");
        poll1.setCreatedBy(admin);
        poll1.getOptions().add(new PollOption("Java"));
        poll1.getOptions().add(new PollOption("Python"));
        poll1.getOptions().add(new PollOption("JavaScript"));
        poll1.getOptions().add(new PollOption("Go"));
        pollRepository.save(poll1);

        Poll poll2 = new Poll();
        poll2.setQuestion("Which frontend framework do you prefer?");
        poll2.setCreatedBy(admin);
        poll2.getOptions().add(new PollOption("React"));
        poll2.getOptions().add(new PollOption("Angular"));
        poll2.getOptions().add(new PollOption("Vue"));
        pollRepository.save(poll2);

        Poll poll3 = new Poll();
        poll3.setQuestion("Best cloud provider for new projects?");
        poll3.setCreatedBy(admin);
        poll3.getOptions().add(new PollOption("AWS"));
        poll3.getOptions().add(new PollOption("Google Cloud"));
        poll3.getOptions().add(new PollOption("Azure"));
        pollRepository.save(poll3);

        System.out.println("=== LivePoll data seeded ===");
        System.out.println("Admin  → admin@livepoll.com / admin123");
        System.out.println("User   → user@livepoll.com  / user123");
    }
}
