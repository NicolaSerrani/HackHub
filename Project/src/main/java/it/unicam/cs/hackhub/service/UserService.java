package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserService {

    private final Map<Long, User> users = new LinkedHashMap<>();
    private long nextUserId = 1;
    private String name;
    private String email;
    private String password;
    private User authenticatedUser;

    public void register(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        if (!validateRegistrationData()) {
            throw new IllegalArgumentException("Invalid registration data");
        }
        User user = new User();
        user.setUserId(nextUserId++);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        users.put(user.getUserId(), user);
    }

    public User login(String email, String password) {
        this.email = email;
        this.password = password;
        if (!validateCredentials()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        authenticatedUser = users.values().stream()
                .filter(user -> user.authenticate(email, password))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        return authenticatedUser;
    }

    public void clearAuthenticationData() {
        if (authenticatedUser != null) {
            authenticatedUser.logout();
        }
        authenticatedUser = null;
        password = null;
    }

    private boolean validateRegistrationData() {
        return checkRequiredFields() && validateEmail() && validatePassword()
                && users.values().stream().noneMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private boolean validateEmail() {
        return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private boolean validatePassword() {
        return password != null && password.length() >= 8;
    }

    private boolean validateCredentials() {
        return checkRequiredFields() && checkAuthentication();
    }

    private boolean checkRequiredFields() {
        return email != null && !email.isBlank() && password != null && !password.isBlank()
                && (name == null || !name.isBlank());
    }

    private boolean checkAuthentication() {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && user.getPassword().equals(password));
    }
}
