package it.unicam.cs.hackhub.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User extends StaffMember {

    private Long userId;
    private String name;
    private String email;
    private String password;
    private boolean authenticated;

    private final List<Invitation> invitations;

    public User() {
        this.invitations = new ArrayList<>();
        this.authenticated = false;
    }

    public void acceptInvitation(Invitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation cannot be null.");
        }

        if (!invitations.contains(invitation)) {
            throw new IllegalArgumentException("Invitation does not belong to this user.");
        }

        invitation.accept();
    }

    public void rejectInvitation(Invitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation cannot be null.");
        }

        if (!invitations.contains(invitation)) {
            throw new IllegalArgumentException("Invitation does not belong to this user.");
        }

        invitation.reject();
    }

    public boolean authenticate(String email, String password) {
        authenticated = Objects.equals(this.email, email)
                && Objects.equals(this.password, password);

        return authenticated;
    }

    public void logout() {
        authenticated = false;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void addInvitation(Invitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation cannot be null.");
        }

        if (!invitations.contains(invitation)) {
            invitations.add(invitation);
            invitation.setReceiver(this);
        }
    }

    public void removeInvitation(Invitation invitation) {
        invitations.remove(invitation);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Invitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }
}