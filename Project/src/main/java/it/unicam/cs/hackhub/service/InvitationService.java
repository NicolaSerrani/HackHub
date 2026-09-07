package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.TeamMember;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import it.unicam.cs.hackhub.repository.InvitationRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public InvitationService(InvitationRepository invitationRepository, UserRepository userRepository,
                             UserService userService) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Invitation> viewReceivedInvitations(Long userId) {
        userService.requireCurrentUser(userId);
        return invitationRepository.findByReceiver_UserId(userId);
    }

    public Invitation acceptTeamInvitation(Long invitationId) {
        TeamMember member = userService.requireRole(TeamMember.class);
        Invitation invitation = findInvitation(invitationId, InvitationType.TEAM);
        requireReceiver(invitation, member.getUserId());
        if (member.hasTeam()) {
            throw new IllegalStateException("A team member already belongs to a team.");
        }
        if (invitation.getTeam() == null) {
            throw new IllegalStateException("Invitation is not associated with a team");
        }
        member.acceptInvitation(invitation);
        invitation.getTeam().addMember(member);
        userRepository.save(member);
        return invitationRepository.save(invitation);
    }

    public Invitation acceptMentorInvitation(Long invitationId) {
        Mentor mentor = userService.requireRole(Mentor.class);
        Invitation invitation = findInvitation(invitationId, InvitationType.MENTOR);
        requireReceiver(invitation, mentor.getUserId());
        return accept(invitation);
    }

    public Invitation acceptJudgeInvitation(Long invitationId) {
        Judge judge = userService.requireRole(Judge.class);
        Invitation invitation = findInvitation(invitationId, InvitationType.JUDGE);
        requireReceiver(invitation, judge.getUserId());
        return accept(invitation);
    }

    private Invitation accept(Invitation invitation) {
        invitation.getReceiver().acceptInvitation(invitation);
        return invitationRepository.save(invitation);
    }

    private Invitation findInvitation(Long invitationId, InvitationType type) {
        Invitation invitation = invitationRepository.findByInvitationIdAndType(invitationId, type)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found or of the wrong type"));
        if (!invitation.isPending()) {
            throw new IllegalStateException("Invitation has already been processed");
        }
        return invitation;
    }

    private void requireReceiver(Invitation invitation, Long userId) {
        if (!invitation.getReceiver().getUserId().equals(userId)) {
            throw new IllegalStateException("Only the invitation recipient can accept it.");
        }
    }
}
