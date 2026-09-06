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
import it.unicam.cs.hackhub.model.enumeration.CallState;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_calls")
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long callId;
    private LocalDateTime dateTime;
    private Duration duration;
    private String link;
    @Enumerated(EnumType.STRING)
    private CallState state;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public Call() {
        this.state = CallState.PROPOSED;
    }

    public void schedule(String link) {
        if (state != CallState.PROPOSED) {
            throw new IllegalStateException("Only proposed calls can be scheduled.");
        }
        if (link == null || link.isBlank()) {
            throw new IllegalArgumentException("Call link cannot be blank.");
        }
        this.link = link;
    }

    public void confirm() {
        if (state != CallState.PROPOSED || link == null) {
            throw new IllegalStateException("Only scheduled proposed calls can be confirmed.");
        }
        state = CallState.CONFIRMED;
    }

    public void cancel() {
        if (state == CallState.CANCELLED) {
            throw new IllegalStateException("Call has already been cancelled.");
        }
        state = CallState.CANCELLED;
    }

    public boolean validate() {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now())
                && duration != null && !duration.isNegative() && !duration.isZero()
                && mentor != null && team != null;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getLink() {
        return link;
    }

    public CallState getState() {
        return state;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor cannot be null.");
        }
        this.mentor = mentor;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }
        this.team = team;
    }
}
