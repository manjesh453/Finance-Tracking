package com.users.config;

import com.users.entity.Users;
import com.users.repo.UserRepo;
import com.users.shared.Role;
import com.users.shared.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class BootStrapAdmin implements CommandLineRunner {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findAll().isEmpty()) {
            Users user = Users.builder()
                    .firstname("Super")
                    .lastname("Admin")
                    .email("superadmin@gmail.com")
                    .fullname("Super Admin")
                    .lastPasswordChanged(new Date())
                    .password(passwordEncoder.encode("Test@1234"))
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .contactNumber("9999999999")
                    .build();

            userRepository.save(user);
        }
    }
}
