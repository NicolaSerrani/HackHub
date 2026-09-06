package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import it.unicam.cs.hackhub.repository.InvitationRepository;
import it.unicam.cs.hackhub.repository.RegistrationRepository;
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
    private final RegistrationRepository registrationRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository,
                       InvitationRepository invitationRepository, RegistrationRepository registrationRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.registrationRepository = registrationRepository;
    }

    public Team createTeam(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be blank");
        }
        if (teamRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalStateException("Team name is already in use");
        }
        Team team = new Team();
        team.setName(name.trim());
        return teamRepository.save(team);
    }

    public List<Invitation> inviteUsers(Long teamId, List<Long> userIds) {
        Team team = findTeam(teamId);
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("At least one user ID is required");
        }
        return userIds.stream().map(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
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

    @Transactional(readOnly = true)
    public List<Team> viewRegisteredTeams(Long hackathonId) {
        return registrationRepository.findActiveTeams(hackathonId);
    }

    public void reportViolation(Long teamId, Long mentorId, String description) {
        Team team = findTeam(teamId);
        Mentor mentor = userRepository.findById(mentorId).filter(Mentor.class::isInstance).map(Mentor.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found: " + mentorId));
        team.reportViolation(mentor, description);
        userRepository.save(mentor);
    }

    @Transactional(readOnly = true)
    public Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
    }
}
