package it.unicam.cs.hackhub.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {

    private Long userId;
    private String name;
    private String email;
    private String passwordHash;

    private final List<Invitation> invitations;

    public User() {
        this.invitations = new ArrayList<>();
    }

    public boolean authenticate(String password) {
        return Objects.equals(passwordHash, password);
    }

    public void acceptInvitation(Invitation invitation) {
        if (invitation != null && invitations.contains(invitation)) {
            invitation.accept();
        }
    }

    public void rejectInvitation(Invitation invitation) {
        if (invitation != null && invitations.contains(invitation)) {
            invitation.reject();
        }
    }

    public void addInvitation(Invitation invitation) {
        if (invitation != null) {
            invitations.add(invitation);
        }
    }

    public void removeInvitation(Invitation invitation) {
        invitations.remove(invitation);
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<Invitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}