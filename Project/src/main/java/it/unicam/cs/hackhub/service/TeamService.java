package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.TeamMember;
import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import it.unicam.cs.hackhub.repository.InvitationRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final UserService userService;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository,
                       InvitationRepository invitationRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.userService = userService;
    }

    public Team createTeam(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be blank");
        }
        if (teamRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalStateException("Team name is already in use");
        }
        TeamMember member = userService.acquireTeamMember();
        if (member.hasTeam()) throw new IllegalStateException("A team member already belongs to a team.");
        Team team = new Team();
        team.setName(name.trim());
        team.addMember(member);
        return teamRepository.save(team);
    }

    public List<Invitation> inviteUsers(Long teamId, List<Long> userIds) {
        requireOwnTeam(teamId);
        Team team = findTeam(teamId);
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("At least one user ID is required");
        }
        return userIds.stream().map(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            if (user instanceof it.unicam.cs.hackhub.model.entity.StaffMember || user.hasTeam()) {
                throw new IllegalStateException("Only a user without a team can be invited.");
            }
            boolean pending = team.getInvitations().stream()
                    .anyMatch(invitation -> invitation.isPending() && invitation.getReceiver().equals(user));
            if (pending) {
                throw new IllegalStateException("User already has a pending invitation for this team: " + userId);
            }
            Invitation invitation = new Invitation();
            invitation.setType(InvitationType.TEAM);
            team.addInvitation(invitation);
            user.addInvitation(invitation);
            return invitationRepository.save(invitation);
        }).toList();
    }

    public void reportViolation(Long teamId, Long mentorId, String description) {
        Mentor mentor = userService.requireMentor();
        if (!mentor.getUserId().equals(mentorId)) {
            throw new IllegalStateException("A mentor can report violations only as themselves.");
        }
        Team team = findTeam(teamId);
        if (team.getSupportMentor() == null || !team.getSupportMentor().getUserId().equals(mentorId)) {
            throw new IllegalStateException("Only the team's mentor can report a violation.");
        }
        team.reportViolation(mentor, description);
        userRepository.save(mentor);
    }

    @Transactional(readOnly = true)
    public Team findTeam(Long teamId) {
        userService.requireAuthenticated();
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
    }

    private TeamMember requireOwnTeam(Long teamId) {
        TeamMember member = userService.requireTeamMember();
        if (!member.getTeam().getTeamId().equals(teamId)) {
            throw new IllegalStateException("A team member can manage only their own team.");
        }
        return member;
    }
}
