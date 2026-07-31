package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.SupportRequest;
import it.unicam.cs.hackhub.model.enumeration.HackathonState;
import it.unicam.cs.hackhub.model.enumeration.SupportRequestState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupportRequestService {

    private final Map<Long, SupportRequest> requests = new LinkedHashMap<>();
    private long nextRequestId = 1;
    private SupportRequest selectedRequest;
    private String response;

    public void createSupportRequest(SupportRequest request) {
        selectedRequest = request;
        if (!checkSupportRequest() || !validateSubject() || !validateDescription()
                || !checkMentorRegistration() || !checkHackathonState() || !checkSupportRequestAvailability()) {
            throw new IllegalArgumentException("Invalid support request");
        }
        selectedRequest.setSupportRequestId(nextRequestId++);
        requests.put(selectedRequest.getSupportRequestId(), selectedRequest);
    }

    public List<SupportRequest> viewSupportRequests(Long mentorId) {
        if (mentorId == null) {
            throw new IllegalArgumentException("Mentor ID cannot be null");
        }
        return requests.values().stream()
                .filter(request -> request.getMentor() != null)
                .toList();
    }

    public void manageSupportRequest(Long requestId, String response, SupportRequestState state) {
        selectedRequest = requests.get(requestId);
        this.response = response;
        if (!checkSupportRequestAvailability() || !validateResponse() || state == null) {
            throw new IllegalArgumentException("Invalid support request management data");
        }
        if (state == SupportRequestState.IN_PROGRESS && selectedRequest.isOpen()) {
            selectedRequest.startHandling();
        }
        selectedRequest.reply(response);
        if (state == SupportRequestState.RESOLVED) {
            selectedRequest.resolve();
        }
    }

    private boolean validateSubject() {
        return selectedRequest.getTitle() != null && !selectedRequest.getTitle().isBlank();
    }

    private boolean validateDescription() {
        return selectedRequest.getDescription() != null && !selectedRequest.getDescription().isBlank();
    }

    private boolean validateResponse() {
        return response != null && !response.isBlank();
    }

    private boolean checkMentorRegistration() {
        return selectedRequest.getMentor() != null;
    }

    private boolean checkHackathonState() {
        return selectedRequest.getHackathon() != null
                && selectedRequest.getHackathon().getState() != HackathonState.COMPLETED;
    }

    private boolean checkSupportRequest() {
        return selectedRequest != null;
    }

    private boolean checkSupportRequestAvailability() {
        return checkSupportRequest() && !selectedRequest.isResolved();
    }
}
