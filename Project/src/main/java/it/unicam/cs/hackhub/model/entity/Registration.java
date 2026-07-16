package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.RegistrationState;

import java.time.LocalDateTime;

public class Registration {

    private Long registrationId;
    private RegistrationState state;
    private LocalDateTime registrationDate;

    private Team team;
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