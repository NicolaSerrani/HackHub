package it.unicam.cs.hackhub.model.entity;

import java.util.ArrayList;
import java.util.List;

public class Mentor extends User {

    public Mentor() {
        super();
    }

    public List<SupportRequest> viewSupportRequests(Hackathon hackathon) {

        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        return new ArrayList<>(hackathon.getSupportRequests());
    }

    public void manageSupportRequest(SupportRequest request,
                                     String response) {

        if (request == null) {
            throw new IllegalArgumentException("Support request cannot be null.");
        }

        request.startHandling();
        request.reply(response);
        request.resolve();
    }

    public boolean canManage(SupportRequest request) {

        if (request == null) {
            return false;
        }

        return !request.isResolved();
    }

}