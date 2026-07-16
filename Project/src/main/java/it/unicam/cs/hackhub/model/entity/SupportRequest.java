package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.SupportRequestState;

import java.time.LocalDateTime;

public class SupportRequest {

    private Long supportRequestId;
    private String title;
    private String description;
    private String response;

    private SupportRequestState state;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    private Team team;
    private Mentor mentor;
    private Hackathon hackathon;

    public SupportRequest() {
        this.state = SupportRequestState.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public boolean validate() {
        return title != null && !title.isBlank()
                && description != null && !description.isBlank();
    }

    public void reply(String response) {

        if (state == SupportRequestState.RESOLVED) {
            throw new IllegalStateException("Support request has already been resolved.");
        }

        this.response = response;
    }

    public void startHandling() {

        if (state != SupportRequestState.OPEN) {
            throw new IllegalStateException("Support request is not open.");
        }

        state = SupportRequestState.IN_PROGRESS;
    }

    public void resolve() {

        if (state == SupportRequestState.RESOLVED) {
            throw new IllegalStateException("Support request has already been resolved.");
        }

        state = SupportRequestState.RESOLVED;
        resolvedAt = LocalDateTime.now();
    }

    public boolean isOpen() {
        return state == SupportRequestState.OPEN;
    }

    public boolean isInProgress() {
        return state == SupportRequestState.IN_PROGRESS;
    }

    public boolean isResolved() {
        return state == SupportRequestState.RESOLVED;
    }

    public Long getSupportRequestId() {
        return supportRequestId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getResponse() {
        return response;
    }

    public SupportRequestState getState() {
        return state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public Team getTeam() {
        return team;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setSupportRequestId(Long supportRequestId) {
        this.supportRequestId = supportRequestId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

}
