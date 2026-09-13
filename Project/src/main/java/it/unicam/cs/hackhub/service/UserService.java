package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.*;
import it.unicam.cs.hackhub.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    // ponytail: one global session; use per-client Spring Security sessions when multiple clients are required.
    private Long loggedInUserId;

    public UserService(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
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
        String accountRole = (role == null ? "USER" : role).trim().toUpperCase(Locale.ROOT);
        if (!accountRole.equals("USER") && !accountRole.equals("STAFF")) {
            throw new IllegalArgumentException("Role must be USER or STAFF");
        }
        User user = accountRole.equals("STAFF") ? new StaffMember() : new User();
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

    public synchronized void logout() {
        if (loggedInUserId == null) {
            throw new IllegalStateException("Login is required.");
        }
        logout(loggedInUserId);
    }

    public synchronized User requireAuthenticated() {
        if (loggedInUserId == null) {
            throw new IllegalStateException("Login is required.");
        }
        return findUser(loggedInUserId);
    }

    public synchronized StaffMember requireStaffMember() {
        User user = requireAuthenticated();
        if (!(user instanceof StaffMember staff)) {
            throw new IllegalStateException("This operation requires a staff member.");
        }
        return staff;
    }

    public synchronized TeamMember requireTeamMember() {
        User user = requireAuthenticated();
        if (!(user instanceof TeamMember member) || !member.hasTeam()) {
            throw new IllegalStateException("This operation requires membership in a team.");
        }
        return member;
    }

    public synchronized Mentor requireMentor() {
        User user = requireAuthenticated();
        if (!(user instanceof Mentor mentor)) throw new IllegalStateException("This operation requires a mentor.");
        return mentor;
    }

    public synchronized Judge requireJudge() {
        User user = requireAuthenticated();
        if (!(user instanceof Judge judge)) throw new IllegalStateException("This operation requires a judge.");
        return judge;
    }

    public synchronized Organizer requireOrganizer() {
        User user = requireAuthenticated();
        if (!(user instanceof Organizer organizer)) {
            throw new IllegalStateException("This operation requires an organizer.");
        }
        return organizer;
    }

    public synchronized TeamMember acquireTeamMember() {
        User user = requireAuthenticated();
        if (user instanceof TeamMember member) return member;
        if (user instanceof StaffMember) throw new IllegalStateException("Only a user can become a team member.");
        return promote(user, "TEAM_MEMBER", TeamMember.class);
    }

    public synchronized Mentor acquireMentor() {
        User user = requireStaffMember();
        if (user instanceof Mentor mentor) return mentor;
        return promoteStaff(user, "MENTOR", Mentor.class);
    }

    public synchronized Judge acquireJudge() {
        User user = requireStaffMember();
        if (user instanceof Judge judge) return judge;
        return promoteStaff(user, "JUDGE", Judge.class);
    }

    public synchronized Organizer acquireOrganizer() {
        User user = requireStaffMember();
        if (user instanceof Organizer organizer) return organizer;
        return promoteStaff(user, "ORGANIZER", Organizer.class);
    }

    private <T extends StaffMember> T promoteStaff(User user, String type, Class<T> target) {
        if (user.getClass() != StaffMember.class) {
            throw new IllegalStateException("The staff member already has a different contextual role.");
        }
        return promote(user, type, target);
    }

    private <T extends User> T promote(User user, String type, Class<T> target) {
        entityManager.flush();
        entityManager.createNativeQuery("update users set user_type = :type where user_id = :id")
                .setParameter("type", type).setParameter("id", user.getUserId()).executeUpdate();
        entityManager.clear();
        return target.cast(findUser(user.getUserId()));
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
