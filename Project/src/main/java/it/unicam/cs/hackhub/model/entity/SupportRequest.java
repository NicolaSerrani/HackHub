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
import it.unicam.cs.hackhub.model.enumeration.SupportRequestState;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_requests")
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supportRequestId;
    private String title;
    private String description;
    private String response;

    @Enumerated(EnumType.STRING)
    private SupportRequestState state;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
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
