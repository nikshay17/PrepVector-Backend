package com.prepVector.backend.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.prepVector.backend.dto.SignUpRequest;
import com.prepVector.backend.model.User;
import com.prepVector.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String signup(SignUpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }


        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        userRepository.save(user);

        return "User registered successfully";
    }

    public boolean validateUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(password, user.getPassword());
    }
}