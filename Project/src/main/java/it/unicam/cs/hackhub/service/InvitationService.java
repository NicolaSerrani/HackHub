package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.*;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import it.unicam.cs.hackhub.model.enumeration.InvitationState;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.InvitationRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InvitationService {
    private final InvitationRepository invitations;
    private final UserRepository users;
    private final HackathonRepository hackathons;
    private final UserService userService;

    public InvitationService(InvitationRepository invitations, UserRepository users,
                             HackathonRepository hackathons, UserService userService) {
        this.invitations = invitations;
        this.users = users;
        this.hackathons = hackathons;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Invitation> viewReceivedInvitations(Long userId) {
        userService.requireCurrentUser(userId);
        return invitations.findByReceiver_UserId(userId);
    }

    public List<InvitationDetails> viewReceivedInvitationDetails(Long userId) {
        return viewReceivedInvitations(userId).stream().map(invitation -> {
            List<String> hackathonNames = invitation.getHackathon() != null
                    ? List.of(invitation.getHackathon().getName())
                    : invitation.getTeam() == null ? List.of() : invitation.getTeam().getRegistrations().stream()
                    .filter(it.unicam.cs.hackhub.model.entity.Registration::isActive)
                    .map(registration -> registration.getHackathon().getName()).distinct().toList();
            return new InvitationDetails(invitation.getInvitationId(), invitation.getType(), invitation.getState(),
                    invitation.getSentAt(), invitation.isPending(), invitation.isAccepted(), invitation.isRejected(),
                    invitation.getTeam() == null ? null : invitation.getTeam().getName(), hackathonNames);
        }).toList();
    }

    public Invitation acceptTeamInvitation(Long invitationId) {
        User user = userService.requireAuthenticated();
        Invitation invitation = requireInvitation(invitationId, InvitationType.TEAM, user);
        if (user instanceof StaffMember || user.hasTeam()) {
            throw new IllegalStateException("The user already belongs to a team or is a staff member.");
        }
        if (invitation.getTeam() == null) throw new IllegalStateException("Invitation has no team");
        TeamMember member = userService.acquireTeamMember();
        invitation = requireInvitation(invitationId, InvitationType.TEAM, member);
        invitation.accept();
        invitation.getTeam().addMember(member);
        users.save(member);
        return invitations.save(invitation);
    }

    public Invitation acceptMentorInvitation(Long invitationId) {
        StaffMember staff = userService.requireStaffMember();
        Invitation invitation = requireInvitation(invitationId, InvitationType.MENTOR, staff);
        Long hackathonId = requireHackathon(invitation).getHackathonId();
        Mentor mentor = userService.acquireMentor();
        invitation = requireInvitation(invitationId, InvitationType.MENTOR, mentor);
        Hackathon hackathon = hackathons.findById(hackathonId).orElseThrow();
        invitation.accept();
        hackathon.addMentor(mentor);
        hackathons.save(hackathon);
        return invitations.save(invitation);
    }

    public Invitation acceptJudgeInvitation(Long invitationId) {
        StaffMember staff = userService.requireStaffMember();
        Invitation invitation = requireInvitation(invitationId, InvitationType.JUDGE, staff);
        Long hackathonId = requireHackathon(invitation).getHackathonId();
        Judge judge = userService.acquireJudge();
        invitation = requireInvitation(invitationId, InvitationType.JUDGE, judge);
        Hackathon hackathon = hackathons.findById(hackathonId).orElseThrow();
        if (hackathon.getJudge() != null) throw new IllegalStateException("A judge is already assigned");
        invitation.accept();
        hackathon.setJudge(judge);
        hackathons.save(hackathon);
        return invitations.save(invitation);
    }

    private Invitation requireInvitation(Long id, InvitationType type, User receiver) {
        Invitation invitation = invitations.findByInvitationIdAndType(id, type)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found or of the wrong type"));
        if (!invitation.isPending()) throw new IllegalStateException("Invitation has already been processed");
        if (!invitation.getReceiver().getUserId().equals(receiver.getUserId()))
            throw new IllegalStateException("Only the invitation recipient can accept it.");
        return invitation;
    }

    private Hackathon requireHackathon(Invitation invitation) {
        if (invitation.getHackathon() == null) throw new IllegalStateException("Invitation has no hackathon");
        return invitation.getHackathon();
    }

    public record InvitationDetails(Long invitationId, InvitationType type, InvitationState state,
                                    java.time.LocalDateTime sentAt, boolean pending, boolean accepted, boolean rejected,
                                    String teamName, List<String> hackathonNames) {}
}
