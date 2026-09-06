package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import it.unicam.cs.hackhub.model.enumeration.RegistrationState;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;
    @Enumerated(EnumType.STRING)
    private RegistrationState state;
    private LocalDateTime registrationDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    public Registration() {
        this.state = RegistrationState.ACTIVE;
        this.registrationDate = LocalDateTime.now();
    }

    public void confirm() {

        if (state != RegistrationState.ACTIVE) {
            throw new IllegalStateException("Registration is not active.");
        }
    }

    public void cancel() {

        if (state != RegistrationState.ACTIVE) {
            throw new IllegalStateException("Registration has already been cancelled.");
        }

        state = RegistrationState.RETIRED;
    }

    public boolean isActive() {
        return state == RegistrationState.ACTIVE;
    }

    public boolean isRetired() {
        return state == RegistrationState.RETIRED;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public RegistrationState getState() {
        return state;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

}
