package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@DiscriminatorValue("USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @JsonIgnore
    private String password;
    @Transient
    private boolean authenticated;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver")
    private List<Invitation> invitations;

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

    public boolean hasTeam() {
        return team != null;
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

    @Transient
    public String getRole() {
        return this instanceof StaffMember ? "STAFF" : "USER";
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = Objects.requireNonNull(team, "Team cannot be null.");
    }

    public List<Invitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }
}
