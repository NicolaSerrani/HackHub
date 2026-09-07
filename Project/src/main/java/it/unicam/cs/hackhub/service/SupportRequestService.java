package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.SupportRequest;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.TeamMember;
import it.unicam.cs.hackhub.model.enumeration.HackathonState;
import it.unicam.cs.hackhub.model.enumeration.SupportRequestState;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.SupportRequestRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupportRequestService {
    private final SupportRequestRepository supportRequestRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final UserService userService;

    public SupportRequestService(SupportRequestRepository supportRequestRepository, TeamRepository teamRepository,
                                 UserRepository userRepository, HackathonRepository hackathonRepository,
                                 UserService userService) {
        this.supportRequestRepository = supportRequestRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
        this.userService = userService;
    }

    public SupportRequest createSupportRequest(Long teamId, Long mentorId, Long hackathonId,
                                               String title, String description) {
        requireOwnTeam(teamId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        Mentor mentor = userRepository.findById(mentorId).filter(Mentor.class::isInstance).map(Mentor.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found: " + mentorId));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found: " + hackathonId));
        SupportRequest request = new SupportRequest();
        request.setTeam(team);
        request.setMentor(mentor);
        request.setHackathon(hackathon);
        request.setTitle(title);
        request.setDescription(description);
        return createSupportRequest(request);
    }

    public SupportRequest createSupportRequest(SupportRequest request) {
        if (request == null || !request.validate() || request.getMentor() == null
                || request.getTeam() == null || request.getHackathon() == null) {
            throw new IllegalArgumentException("Invalid support request");
        }
        requireOwnTeam(request.getTeam().getTeamId());
        if (request.getHackathon().getState() == HackathonState.COMPLETED) {
            throw new IllegalStateException("Cannot request support for a completed hackathon");
        }
        return supportRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<SupportRequest> viewSupportRequests(Long mentorId) {
        Mentor mentor = userService.requireRole(Mentor.class);
        if (!mentor.getUserId().equals(mentorId)) {
            throw new IllegalStateException("A mentor can view only their support requests.");
        }
        return supportRequestRepository.findByMentor_UserId(mentorId);
    }

    @Transactional(readOnly = true)
    public List<SupportRequest> viewHackathonSupportRequests(Long hackathonId) {
        userService.requireRole(Mentor.class);
        return supportRequestRepository.findByHackathon_HackathonId(hackathonId);
    }

    public SupportRequest manageSupportRequest(Long requestId, String response, SupportRequestState state) {
        Mentor mentor = userService.requireRole(Mentor.class);
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found: " + requestId));
        if (!mentor.getUserId().equals(request.getMentor().getUserId())) {
            throw new IllegalStateException("A mentor can manage only their support requests.");
        }
        if (response == null || response.isBlank() || state == null) {
            throw new IllegalArgumentException("Response and state are required");
        }
        if (state == SupportRequestState.IN_PROGRESS && request.isOpen()) {
            request.startHandling();
        }
        request.reply(response);
        if (state == SupportRequestState.RESOLVED) {
            request.resolve();
        }
        return supportRequestRepository.save(request);
    }

    private TeamMember requireOwnTeam(Long teamId) {
        TeamMember member = userService.requireRole(TeamMember.class);
        if (!member.getTeam().getTeamId().equals(teamId)) {
            throw new IllegalStateException("A team member can request support only for their own team.");
        }
        return member;
    }
}
