package it.unicam.cs.hackhub.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mentor extends StaffMember {

    private final List<String> reportedViolations;

    public Mentor() {
        super();
        this.reportedViolations = new ArrayList<>();
    }

    public List<SupportRequest> viewSupportRequests(Hackathon hackathon) {
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        return hackathon.getSupportRequests();
    }

    public void reportViolation(Team team, String description) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Violation description cannot be null or blank."
            );
        }

        reportedViolations.add(
                "Team: " + team.getName() + " - " + description
        );
    }

    public List<String> getReportedViolations() {
        return Collections.unmodifiableList(reportedViolations);
    }
}