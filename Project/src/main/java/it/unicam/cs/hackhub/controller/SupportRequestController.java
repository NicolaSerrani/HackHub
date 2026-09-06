package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.SupportRequest;
import it.unicam.cs.hackhub.model.enumeration.SupportRequestState;
import it.unicam.cs.hackhub.service.SupportRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/support-requests")
public class SupportRequestController {
    private final SupportRequestService supportRequestService;

    public SupportRequestController(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportRequest createSupportRequest(@RequestBody CreateSupportRequest request) {
        return supportRequestService.createSupportRequest(request.teamId(), request.mentorId(), request.hackathonId(),
                request.title(), request.description());
    }

    @GetMapping("/mentor/{mentorId}")
    public List<SupportRequest> viewMentorRequests(@PathVariable Long mentorId) {
        return supportRequestService.viewSupportRequests(mentorId);
    }

    @GetMapping("/hackathon/{hackathonId}")
    public List<SupportRequest> viewHackathonRequests(@PathVariable Long hackathonId) {
        return supportRequestService.viewHackathonSupportRequests(hackathonId);
    }

    @PutMapping("/{requestId}")
    public SupportRequest manageSupportRequest(@PathVariable Long requestId, @RequestBody ManageRequest request) {
        return supportRequestService.manageSupportRequest(requestId, request.response(), request.state());
    }

    public record CreateSupportRequest(Long teamId, Long mentorId, Long hackathonId,
                                       String title, String description) {}
    public record ManageRequest(String response, SupportRequestState state) {}
}
