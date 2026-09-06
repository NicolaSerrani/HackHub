package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;
    @Column(nullable = false, unique = true)
    private String name;
    private LocalDate createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamMember> members;
    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Invitation> invitations;
    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Registration> registrations;
    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<SupportRequest> supportRequests;
    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Call> calls;
    @JsonIgnore
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Payment> payments;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_mentor_id")
    private Mentor supportMentor;

    public Team() {
        this.createdAt = LocalDate.now();
        this.members = new ArrayList<>();
        this.invitations = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.supportRequests = new ArrayList<>();
        this.calls = new ArrayList<>();
        this.payments = new ArrayList<>();
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
            invitation.setTeam(this);
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

    public boolean hasRegisteredTeams() {
        return registrations.stream().anyMatch(Registration::isActive);
    }

    public void addCall(Call call) {
        if (call == null) {
            throw new IllegalArgumentException("Call cannot be null.");
        }
        if (!calls.contains(call)) {
            calls.add(call);
            call.setTeam(this);
        }
    }

    public void addPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null.");
        }
        if (!payments.contains(payment)) {
            payments.add(payment);
            payment.setRecipient(this);
        }
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

    public List<Call> getCalls() {
        return Collections.unmodifiableList(calls);
    }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public Mentor getSupportMentor() {
        return supportMentor;
    }

    public void setSupportMentor(Mentor supportMentor) {
        this.supportMentor = supportMentor;
    }

    @JsonIgnore
    public int getMemberCount() {
        return members.size();
    }
}
