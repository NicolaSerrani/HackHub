package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Organizer;
import it.unicam.cs.hackhub.model.entity.TeamMember;
import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String name, String email, String password) {
        return register(name, email, password, "USER");
    }

    public User register(String name, String email, String password, String role) {
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Name and email are required");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("Email is already registered");
        }
        String normalizedRole = role == null ? "USER" : role.trim().toUpperCase(Locale.ROOT);
        User user = switch (normalizedRole) {
            case "USER" -> new User();
            case "TEAM_MEMBER" -> new TeamMember();
            case "MENTOR" -> new Mentor();
            case "JUDGE" -> new Judge();
            case "ORGANIZER" -> new Organizer();
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        };
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase(Locale.ROOT));
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(email == null ? "" : email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!user.authenticate(email, password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
