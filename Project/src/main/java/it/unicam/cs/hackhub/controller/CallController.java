package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Call;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.service.CallService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CallController {

    private final CallService callService = new CallService();

    public Call proposeCall(Mentor mentor, Team team, LocalDateTime dateTime, Duration duration) {
        return callService.proposeCall(mentor, team, dateTime, duration);
    }

    public void confirmCall(Long callId) {
        callService.confirmCall(callId);
    }

    public void cancelCall(Long callId) {
        callService.cancelCall(callId);
    }

    public List<Call> viewTeamCalls(Long teamId) {
        return callService.viewTeamCalls(teamId);
    }

    public List<LocalDateTime> viewAvailableTimeSlots() {
        return callService.viewAvailableTimeSlots();
    }
}
