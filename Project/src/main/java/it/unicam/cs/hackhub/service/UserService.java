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
    // ponytail: one global session; use per-client Spring Security sessions when multiple clients are required.
    private Long loggedInUserId;

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
    public synchronized User login(String email, String password) {
        if (loggedInUserId != null) {
            throw new IllegalStateException("An user is already logged in. Logout is required first.");
        }
        User user = userRepository.findByEmailIgnoreCase(email == null ? "" : email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!user.authenticate(email, password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        loggedInUserId = user.getUserId();
        return user;
    }

    public synchronized void logout(Long userId) {
        if (!userId.equals(loggedInUserId)) {
            throw new IllegalStateException("Only the logged-in user can log out.");
        }
        findUser(userId).logout();
        loggedInUserId = null;
    }

    public synchronized User requireAuthenticated() {
        if (loggedInUserId == null) {
            throw new IllegalStateException("Login is required.");
        }
        return findUser(loggedInUserId);
    }

    public synchronized <T extends User> T requireRole(Class<T> role) {
        User user = requireAuthenticated();
        if (!role.isInstance(user)) {
            throw new IllegalStateException("This operation requires role: " + role.getSimpleName());
        }
        return role.cast(user);
    }

    public synchronized void requireCurrentUser(Long userId) {
        if (!requireAuthenticated().getUserId().equals(userId)) {
            throw new IllegalStateException("This operation is allowed only for the logged-in user.");
        }
    }

    @Transactional(readOnly = true)
    public User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
