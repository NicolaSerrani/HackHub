package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.InvitationState;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;

import java.time.LocalDateTime;

public class Invitation {

    private Long invitationId;
    private InvitationType type;
    private InvitationState state;
    private LocalDateTime sentAt;

    private User sender;
    private User receiver;

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

}
