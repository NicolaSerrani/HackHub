package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.integration.calendar.Calendar;
import it.unicam.cs.hackhub.integration.calendar.LocalCalendar;
import it.unicam.cs.hackhub.model.entity.Call;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.repository.CallRepository;
import it.unicam.cs.hackhub.repository.memory.InMemoryCallRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CallService {

    private final List<Call> calls = new ArrayList<>();
    private final Calendar calendar;
    private final CallRepository callRepository;
    private long nextCallId = 1;

    public CallService() {
        this(new LocalCalendar(), new InMemoryCallRepository());
    }

    public CallService(Calendar calendar) {
        this(calendar, new InMemoryCallRepository());
    }

    public CallService(Calendar calendar, CallRepository callRepository) {
        if (calendar == null) {
            throw new IllegalArgumentException("Calendar cannot be null.");
        }
        if (callRepository == null) {
            throw new IllegalArgumentException("Call repository cannot be null.");
        }
        this.calendar = calendar;
        this.callRepository = callRepository;
    }

    public Call proposeCall(Mentor mentor, Team team, LocalDateTime dateTime, Duration duration) {
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor cannot be null.");
        }
        Call call = mentor.proposeCall(team, dateTime, duration);
        call.setCallId(nextCallId++);
        callRepository.save(call);
        call.schedule(calendar.registerCall(call));
        callRepository.save(call);
        calls.add(call);
        return call;
    }

    public void confirmCall(Long callId) {
        Call call = findCall(callId);
        call.confirm();
        callRepository.save(call);
    }

    public void cancelCall(Long callId) {
        Call call = findCall(callId);
        call.cancel();
        callRepository.save(call);
    }

    public List<Call> viewTeamCalls(Long teamId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team ID cannot be null.");
        }
        return callRepository.findByTeam(teamId);
    }

    public List<Call> viewCalls() {
        return new ArrayList<>(calls);
    }

    public Call findCall(Long callId) {
        if (callId == null) {
            throw new IllegalArgumentException("Call ID cannot be null.");
        }
        Call call = callRepository.findById(callId);
        if (call == null) {
            throw new IllegalArgumentException("Call not found: " + callId);
        }
        return call;
    }

    public List<LocalDateTime> viewAvailableTimeSlots() {
        return calendar.getAvailableTimeSlots();
    }
}
