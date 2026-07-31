package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.service.UserService;

public class UserController {

    private final UserService userService = new UserService();

    public void register(String name, String email, String password) {
        userService.register(name, email, password);
    }

    public User login(String email, String password) {
        return userService.login(email, password);
    }
}
