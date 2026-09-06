package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Call;
import it.unicam.cs.hackhub.service.CallService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calls")
public class CallController {
    private final CallService callService;

    public CallController(CallService callService) {
        this.callService = callService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Call proposeCall(@RequestBody ProposeCallRequest request) {
        return callService.proposeCall(request.mentorId(), request.teamId(), request.dateTime(),
                Duration.ofMinutes(request.durationMinutes()));
    }

    @PostMapping("/{callId}/confirm")
    public Call confirmCall(@PathVariable Long callId) {
        return callService.confirmCall(callId);
    }

    @PostMapping("/{callId}/cancel")
    public Call cancelCall(@PathVariable Long callId) {
        return callService.cancelCall(callId);
    }

    @GetMapping("/team/{teamId}")
    public List<Call> viewTeamCalls(@PathVariable Long teamId) {
        return callService.viewTeamCalls(teamId);
    }

    @GetMapping("/available-slots")
    public List<LocalDateTime> viewAvailableTimeSlots() {
        return callService.viewAvailableTimeSlots();
    }

    public record ProposeCallRequest(Long mentorId, Long teamId, LocalDateTime dateTime,
                                     long durationMinutes) {}
}
