package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.service.InvitationService;

import java.util.List;

public class InvitationController {

    private final InvitationService invitationService = new InvitationService();

    public List<Invitation> viewReceivedInvitations(Long userId) {
        return invitationService.viewReceivedInvitations(userId);
    }

    public void acceptTeamInvitation(Long invitationId) {
        invitationService.acceptTeamInvitation(invitationId);
    }

    public void acceptMentorInvitation(Long invitationId) {
        invitationService.acceptMentorInvitation(invitationId);
    }

    public void acceptJudgeInvitation(Long invitationId) {
        invitationService.acceptJudgeInvitation(invitationId);
    }
}
