package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.integration.calendar.Calendar;
import it.unicam.cs.hackhub.model.entity.Call;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
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

    public CallService(Calendar calendar, CallRepository callRepository,
                       UserRepository userRepository, TeamRepository teamRepository) {
        this.calendar = calendar;
        this.callRepository = callRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public Call proposeCall(Long mentorId, Long teamId, LocalDateTime dateTime, Duration duration) {
        Mentor mentor = userRepository.findById(mentorId).filter(Mentor.class::isInstance)
                .map(Mentor.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found: " + mentorId));
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
        call.confirm();
        return callRepository.save(call);
    }

    public Call cancelCall(Long callId) {
        Call call = findCall(callId);
        call.cancel();
        return callRepository.save(call);
    }

    @Transactional(readOnly = true)
    public List<Call> viewTeamCalls(Long teamId) {
        return callRepository.findByTeam_TeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<Call> viewCalls() {
        return callRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Call findCall(Long callId) {
        return callRepository.findById(callId)
                .orElseThrow(() -> new IllegalArgumentException("Call not found: " + callId));
    }

    public List<LocalDateTime> viewAvailableTimeSlots() {
        return calendar.getAvailableTimeSlots();
    }
}
