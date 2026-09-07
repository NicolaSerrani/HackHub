package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request.name(), request.email(), request.password(), request.role());
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return userService.login(request.email(), request.password());
    }

    @PostMapping("/{userId}/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@PathVariable Long userId) {
        userService.logout(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        userService.requireCurrentUser(userId);
        return userService.findUser(userId);
    }

    public record RegisterRequest(String name, String email, String password, String role) {}
    public record LoginRequest(String email, String password) {}
}
