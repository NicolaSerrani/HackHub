package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import it.unicam.cs.hackhub.model.enumeration.HackathonState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "hackathons")
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hackathonId;
    @Column(nullable = false, unique = true)
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String regulation;
    private String location;
    private LocalDate registrationDeadline;
    private Double prize;
    private int maxTeamMembers;
    @Enumerated(EnumType.STRING)
    private HackathonState state;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winner;
    @JsonIgnore
    @OneToOne(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private Payment payment;
    private boolean leaderboardPublished;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id")
    private Judge judge;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "hackathon_mentors",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id"))
    private List<Mentor> mentors;
    @JsonIgnore
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<Registration> registrations;
    @JsonIgnore
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<SupportRequest> supportRequests;
    @JsonIgnore
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<Submission> submissions;

    public Hackathon() {
        this.state = HackathonState.REGISTRATION_OPEN;
        this.leaderboardPublished = false;
        this.mentors = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.supportRequests = new ArrayList<>();
        this.submissions = new ArrayList<>();
    }

    public void registerTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        if (state != HackathonState.REGISTRATION_OPEN) {
            throw new IllegalStateException("Registrations are not open.");
        }

        if (team.getMemberCount() > maxTeamMembers) {
            throw new IllegalArgumentException("The team exceeds the maximum number of members.");
        }

        boolean alreadyRegistered = registrations.stream()
                .anyMatch(registration -> registration.getTeam() == team
                        && registration.isActive());

        if (alreadyRegistered) {
            throw new IllegalStateException("The team is already registered.");
        }

        Registration registration = new Registration();
        registration.setTeam(team);
        registration.setHackathon(this);

        registrations.add(registration);
    }

    public void setWinner(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Winner team cannot be null.");
        }

        this.winner = team;
    }

    public void setState(HackathonState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null.");
        }

        this.state = state;
    }

    public void addMentor(Mentor mentor) {
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor cannot be null.");
        }

        if (!mentors.contains(mentor)) {
            mentors.add(mentor);
        }
    }

    public void setJudge(Judge judge) {
        if (judge == null) {
            throw new IllegalArgumentException("Judge cannot be null.");
        }

        this.judge = judge;
    }

    public void addSupportRequest(SupportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Support request cannot be null.");
        }

        if (!supportRequests.contains(request)) {
            supportRequests.add(request);
            request.setHackathon(this);
        }
    }

    public void addSubmission(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        if (!submissions.contains(submission)) {
            submissions.add(submission);
        }
    }

    public void publishLeaderboard() {
        if (state != HackathonState.UNDER_EVALUATION) {
            throw new IllegalStateException(
                    "The leaderboard can be published only during evaluation."
            );
        }

        if (winner == null) {
            throw new IllegalStateException(
                    "A winner must be declared before publishing the leaderboard."
            );
        }

        leaderboardPublished = true;
        state = HackathonState.COMPLETED;
    }

    public Payment awardPrize() {
        if (winner == null) {
            throw new IllegalStateException("A winner must be declared before awarding the prize.");
        }
        if (payment != null) {
            throw new IllegalStateException("The prize payment has already been created.");
        }

        Payment prizePayment = new Payment();
        prizePayment.setAmount(prize == null ? 0 : prize);
        prizePayment.setHackathon(this);
        winner.addPayment(prizePayment);
        this.payment = prizePayment;
        return prizePayment;
    }
    public List<Team> getLeaderboard() {
        if (winner == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(winner);
    }

    public HackathonState getState() {
        return state;
    }

    @JsonIgnore
    public Hackathon getDetails() {
        return this;
    }

    public Long getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(Long hackathonId) {
        this.hackathonId = hackathonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be null or blank.");
        }
        this.location = location;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Double getPrize() {
        return prize;
    }

    public void setPrize(Double prize) {
        this.prize = prize;
    }

    public int getMaxTeamMembers() {
        return maxTeamMembers;
    }

    public void setMaxTeamMembers(int maxTeamMembers) {
        this.maxTeamMembers = maxTeamMembers;
    }

    public Team getWinner() {
        return winner;
    }

    public Payment getPayment() {
        return payment;
    }

    public boolean isLeaderboardPublished() {
        return leaderboardPublished;
    }

    public Judge getJudge() {
        return judge;
    }

    public List<Mentor> getMentors() {
        return Collections.unmodifiableList(mentors);
    }

    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(registrations);
    }

    public List<SupportRequest> getSupportRequests() {
        return Collections.unmodifiableList(supportRequests);
    }

    public List<Submission> getSubmissions() {
        return Collections.unmodifiableList(submissions);
    }
}
