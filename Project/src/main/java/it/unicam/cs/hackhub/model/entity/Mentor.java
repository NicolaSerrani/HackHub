package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MENTOR")
public class Mentor extends StaffMember {
    @ElementCollection
    @CollectionTable(name = "mentor_violations", joinColumns = @JoinColumn(name = "mentor_id"))
    @Column(name = "description")
    private List<String> reportedViolations = new ArrayList<>();

    public void reportViolation(Team team, String description) {
        if (team == null || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Team and violation description are required.");
        }
        reportedViolations.add("Team: " + team.getName() + " - " + description);
    }

    @JsonIgnore
    public List<String> getReportedViolations() {
        return List.copyOf(reportedViolations);
    }
}
