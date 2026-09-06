package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.TeamMember;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import it.unicam.cs.hackhub.repository.InvitationRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.List;

@Service
@Transactional
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;

    public InvitationService(InvitationRepository invitationRepository, UserRepository userRepository) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Invitation> viewReceivedInvitations(Long userId) {
        return invitationRepository.findByReceiver_UserId(userId);
    }

    public Invitation acceptTeamInvitation(Long invitationId) {
        Invitation invitation = findInvitation(invitationId, InvitationType.TEAM);
        Object receiver = Hibernate.unproxy(invitation.getReceiver());
        if (!(receiver instanceof TeamMember member)) {
            throw new IllegalStateException("A team invitation can only be accepted by a TEAM_MEMBER");
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
        return accept(findInvitation(invitationId, InvitationType.MENTOR));
    }

    public Invitation acceptJudgeInvitation(Long invitationId) {
        return accept(findInvitation(invitationId, InvitationType.JUDGE));
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
}
