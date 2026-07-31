package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.SupportRequest;
import it.unicam.cs.hackhub.model.enumeration.SupportRequestState;
import it.unicam.cs.hackhub.service.SupportRequestService;

import java.util.List;

public class SupportRequestController {

    private final SupportRequestService supportRequestService = new SupportRequestService();

    public void createSupportRequest(SupportRequest request) {
        supportRequestService.createSupportRequest(request);
    }

    public List<SupportRequest> viewSupportRequest(Long hackathonId) {
        return supportRequestService.viewSupportRequests(hackathonId);
    }

    public void manageSupportRequest(Long requestId, String response, SupportRequestState state) {
        supportRequestService.manageSupportRequest(requestId, response, state);
    }
}
