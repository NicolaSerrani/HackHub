package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.integration.calendar.Calendar;
import it.unicam.cs.hackhub.model.entity.Call;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.TeamMember;
import it.unicam.cs.hackhub.repository.CallRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CallService {
    private final Calendar calendar;
    private final CallRepository callRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;

    public CallService(Calendar calendar, CallRepository callRepository,
                       UserRepository userRepository, TeamRepository teamRepository, UserService userService) {
        this.calendar = calendar;
        this.callRepository = callRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    public Call proposeCall(Long mentorId, Long teamId, LocalDateTime dateTime, Duration duration) {
        Mentor mentor = userService.requireRole(Mentor.class);
        if (!mentor.getUserId().equals(mentorId)) {
            throw new IllegalStateException("A mentor can propose calls only as themselves.");
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        return proposeCall(mentor, team, dateTime, duration);
    }

    public Call proposeCall(Mentor mentor, Team team, LocalDateTime dateTime, Duration duration) {
        Call call = mentor.proposeCall(team, dateTime, duration);
        callRepository.saveAndFlush(call);
        call.schedule(calendar.registerCall(call));
        return callRepository.save(call);
    }

    public Call confirmCall(Long callId) {
        Call call = findCall(callId);
        TeamMember member = userService.requireRole(TeamMember.class);
        if (!member.getTeam().getTeamId().equals(call.getTeam().getTeamId())) {
            throw new IllegalStateException("Only a member of the team can confirm this call.");
        }
        call.confirm();
        return callRepository.save(call);
    }

    public Call cancelCall(Long callId) {
        Call call = findCall(callId);
        Mentor mentor = userService.requireRole(Mentor.class);
        if (!mentor.getUserId().equals(call.getMentor().getUserId())) {
            throw new IllegalStateException("Only the proposing mentor can cancel this call.");
        }
        call.cancel();
        return callRepository.save(call);
    }

    @Transactional(readOnly = true)
    public List<Call> viewTeamCalls(Long teamId) {
        TeamMember member = userService.requireRole(TeamMember.class);
        if (!member.getTeam().getTeamId().equals(teamId)) {
            throw new IllegalStateException("A team member can view only their team's calls.");
        }
        return callRepository.findByTeam_TeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<Call> viewCalls() {
        userService.requireAuthenticated();
        return callRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Call findCall(Long callId) {
        userService.requireAuthenticated();
        return callRepository.findById(callId)
                .orElseThrow(() -> new IllegalArgumentException("Call not found: " + callId));
    }

    public List<LocalDateTime> viewAvailableTimeSlots() {
        userService.requireRole(Mentor.class);
        return calendar.getAvailableTimeSlots();
    }
}
