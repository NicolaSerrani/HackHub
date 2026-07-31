package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class InvitationService {

    private static final Map<Long, Invitation> invitations = new LinkedHashMap<>();
    private static final Set<Long> assignedMentors = new HashSet<>();
    private static final Set<Long> assignedJudges = new HashSet<>();
    private static long nextInvitationId = 1;
    private Invitation selectedInvitation;

    public List<Invitation> viewReceivedInvitations(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return invitations.values().stream()
                .filter(invitation -> invitation.getReceiver() != null
                        && userId.equals(invitation.getReceiver().getUserId()))
                .toList();
    }

    public void acceptTeamInvitation(Long invitationId) {
        selectedInvitation = findInvitation(invitationId, InvitationType.TEAM);
        if (!checkInvitationValidity() || checkUserAlreadyInTeam()) {
            throw new IllegalStateException("Team invitation cannot be accepted");
        }
        selectedInvitation.getReceiver().acceptInvitation(selectedInvitation);
    }

    public void acceptMentorInvitation(Long invitationId) {
        selectedInvitation = findInvitation(invitationId, InvitationType.MENTOR);
        if (!checkInvitationValidity() || checkMentorAlreadyAssigned()) {
            throw new IllegalStateException("Mentor invitation cannot be accepted");
        }
        selectedInvitation.getReceiver().acceptInvitation(selectedInvitation);
        assignedMentors.add(selectedInvitation.getReceiver().getUserId());
    }

    public void acceptJudgeInvitation(Long invitationId) {
        selectedInvitation = findInvitation(invitationId, InvitationType.JUDGE);
        if (!checkInvitationValidity() || checkJudgeAlreadyAssigned()) {
            throw new IllegalStateException("Judge invitation cannot be accepted");
        }
        selectedInvitation.getReceiver().acceptInvitation(selectedInvitation);
        assignedJudges.add(selectedInvitation.getReceiver().getUserId());
    }

    static long nextInvitationId() {
        return nextInvitationId++;
    }

    static void storeInvitation(Invitation invitation) {
        invitations.put(invitation.getInvitationId(), invitation);
    }

    private boolean checkInvitationValidity() {
        return selectedInvitation != null && selectedInvitation.isPending()
                && selectedInvitation.getReceiver() != null;
    }

    private boolean checkUserAlreadyInTeam() {
        return false;
    }

    private boolean checkMentorAlreadyAssigned() {
        return assignedMentors.contains(selectedInvitation.getReceiver().getUserId());
    }

    private boolean checkJudgeAlreadyAssigned() {
        return assignedJudges.contains(selectedInvitation.getReceiver().getUserId());
    }

    private Invitation findInvitation(Long invitationId, InvitationType type) {
        Invitation invitation = invitations.get(invitationId);
        if (invitation == null || invitation.getType() != type) {
            throw new IllegalArgumentException("Invitation not found or of the wrong type");
        }
        return invitation;
    }
}
