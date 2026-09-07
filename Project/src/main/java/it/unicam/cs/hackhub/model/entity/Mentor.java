package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MENTOR")
public class Mentor extends StaffMember {

    @ElementCollection
    @CollectionTable(name = "mentor_violations", joinColumns = @JoinColumn(name = "mentor_id"))
    @Column(name = "description")
    private List<String> reportedViolations;
    @JsonIgnore
    @OneToMany(mappedBy = "mentor")
    private List<Call> proposedCalls;
    @JsonIgnore
    @OneToMany(mappedBy = "supportMentor")
    private List<Team> supportedTeams;

    public Mentor() {
        super();
        this.reportedViolations = new ArrayList<>();
        this.proposedCalls = new ArrayList<>();
        this.supportedTeams = new ArrayList<>();
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

    public Call proposeCall(Team team, LocalDateTime dateTime, Duration duration) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        Call call = new Call();
        call.setMentor(this);
        call.setTeam(team);
        call.setDateTime(dateTime);
        call.setDuration(duration);
        if (!call.validate()) {
            throw new IllegalArgumentException("Invalid call data.");
        }

        proposedCalls.add(call);
        team.addCall(call);
        return call;
    }

    public void supportTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }
        if (team.getSupportMentor() != null && team.getSupportMentor() != this) {
            throw new IllegalStateException("Team already has a support mentor.");
        }
        if (!supportedTeams.contains(team)) {
            supportedTeams.add(team);
            team.setSupportMentor(this);
        }
    }

    @JsonIgnore
    public List<String> getReportedViolations() {
        return Collections.unmodifiableList(reportedViolations);
    }

    public List<Call> getProposedCalls() {
        return Collections.unmodifiableList(proposedCalls);
    }

    public List<Team> getSupportedTeams() {
        return Collections.unmodifiableList(supportedTeams);
    }
}
