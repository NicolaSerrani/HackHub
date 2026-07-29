package it.unicam.cs.hackhub.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

    private Long teamId;
    private String name;
    private LocalDate createdAt;

    private final List<TeamMember> members;
    private final List<Invitation> invitations;
    private final List<Registration> registrations;
    private final List<SupportRequest> supportRequests;

    public Team() {
        this.createdAt = LocalDate.now();
        this.members = new ArrayList<>();
        this.invitations = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.supportRequests = new ArrayList<>();
    }

    public void addMember(TeamMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Team member cannot be null.");
        }

        if (!members.contains(member)) {
            members.add(member);
            member.setTeam(this);
        }
    }

    public void removeMember(TeamMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Team member cannot be null.");
        }

        members.remove(member);
    }

    public void addInvitation(Invitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation cannot be null.");
        }

        if (!invitations.contains(invitation)) {
            invitations.add(invitation);
        }
    }

    public void addRegistration(Registration registration) {
        if (registration == null) {
            throw new IllegalArgumentException("Registration cannot be null.");
        }

        if (!registrations.contains(registration)) {
            registrations.add(registration);
            registration.setTeam(this);
        }
    }

    public void addSupportRequest(SupportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Support request cannot be null."
            );
        }

        if (!supportRequests.contains(request)) {
            supportRequests.add(request);
            request.setTeam(this);
        }
    }

    public boolean hasRegistratedTeams() {
        return registrations.stream().anyMatch(Registration::isActive);
    }

    public void reportViolation(Mentor mentor, String description) {
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor cannot be null.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Violation description cannot be null or blank."
            );
        }

        mentor.reportViolation(this, description);
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Team name cannot be null or blank."
            );
        }

        this.name = name;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public List<TeamMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<Invitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }

    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(registrations);
    }

    public List<SupportRequest> getSupportRequests() {
        return Collections.unmodifiableList(supportRequests);
    }

    public int getMemberCount() {
        return members.size();
    }
}