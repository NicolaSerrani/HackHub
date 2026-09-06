package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.service.InvitationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/received/{userId}")
    public List<Invitation> viewReceivedInvitations(@PathVariable Long userId) {
        return invitationService.viewReceivedInvitations(userId);
    }

    @PostMapping("/{invitationId}/accept/team")
    public Invitation acceptTeamInvitation(@PathVariable Long invitationId) {
        return invitationService.acceptTeamInvitation(invitationId);
    }

    @PostMapping("/{invitationId}/accept/mentor")
    public Invitation acceptMentorInvitation(@PathVariable Long invitationId) {
        return invitationService.acceptMentorInvitation(invitationId);
    }

    @PostMapping("/{invitationId}/accept/judge")
    public Invitation acceptJudgeInvitation(@PathVariable Long invitationId) {
        return invitationService.acceptJudgeInvitation(invitationId);
    }
}
