package it.unicam.cs.hackhub.controller;

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
    public List<InvitationService.InvitationDetails> viewReceivedInvitations(@PathVariable("userId") Long userId) {
        return invitationService.viewReceivedInvitationDetails(userId);
    }

    @PostMapping("/{invitationId}/accept/team")
    public it.unicam.cs.hackhub.model.entity.Invitation acceptTeamInvitation(@PathVariable("invitationId") Long invitationId) {
        return invitationService.acceptTeamInvitation(invitationId);
    }

    @PostMapping("/{invitationId}/accept/mentor")
    public it.unicam.cs.hackhub.model.entity.Invitation acceptMentorInvitation(@PathVariable("invitationId") Long invitationId) {
        return invitationService.acceptMentorInvitation(invitationId);
    }

    @PostMapping("/{invitationId}/accept/judge")
    public it.unicam.cs.hackhub.model.entity.Invitation acceptJudgeInvitation(@PathVariable("invitationId") Long invitationId) {
        return invitationService.acceptJudgeInvitation(invitationId);
    }
}
