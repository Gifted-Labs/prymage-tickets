package com.giftedlabs.prymageproduct.config;

import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Bean
    CommandLineRunner seedAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin.email}") String email,
            @Value("${app.seed.admin.password}") String password,
            @Value("${app.seed.admin.name}") String name
    ) {
        return args -> {
            if (userRepository.existsByRole(Role.ADMIN)) {
                return;
            }

            User admin = new User();
            admin.setFullName(name);
            admin.setEmail(email.toLowerCase());
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);
            log.warn("Seeded default admin account: {}. Change this password immediately.", email);
        };
    }
}
