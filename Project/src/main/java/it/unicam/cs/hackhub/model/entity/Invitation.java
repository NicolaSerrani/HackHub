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
import it.unicam.cs.hackhub.model.enumeration.InvitationState;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;
    @Enumerated(EnumType.STRING)
    private InvitationType type;
    @Enumerated(EnumType.STRING)
    private InvitationState state;
    private LocalDateTime sentAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Invitation() {
        this.state = InvitationState.PENDING;
        this.sentAt = LocalDateTime.now();
    }

    public void accept() {

        if (state != InvitationState.PENDING) {
            throw new IllegalStateException("Invitation has already been processed.");
        }

        state = InvitationState.ACCEPTED;
    }

    public void reject() {

        if (state != InvitationState.PENDING) {
            throw new IllegalStateException("Invitation has already been processed.");
        }

        state = InvitationState.REJECTED;
    }

    public boolean isPending() {
        return state == InvitationState.PENDING;
    }

    public boolean isAccepted() {
        return state == InvitationState.ACCEPTED;
    }

    public boolean isRejected() {
        return state == InvitationState.REJECTED;
    }

    public Long getInvitationId() {
        return invitationId;
    }

    public InvitationType getType() {
        return type;
    }

    public InvitationState getState() {
        return state;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public void setType(InvitationType type) {
        this.type = type;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
