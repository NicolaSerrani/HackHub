package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.HackathonState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hackathon {

    private Long hackathonId;
    private String name;
    private String regulation;
    private String location;

    private LocalDate registrationDeadline;
    private LocalDate startDate;
    private LocalDate endDate;

    private double prize;
    private int maxTeamMembers;

    private HackathonState state;
    private boolean leaderboardPublished;

    private Judge judge;
    private Team winner;

    private final List<Mentor> mentors;
    private final List<Registration> registrations;
    private final List<Submission> submissions;
    private final List<SupportRequest> supportRequests;

    public Hackathon() {
        this.mentors = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.supportRequests = new ArrayList<>();
        this.state = HackathonState.REGISTRATION_OPEN;
        this.leaderboardPublished = false;
    }

    public void registerTeam(Registration registration) {
        registrations.add(registration);
    }

    public void addSubmission(Submission submission) {
        submissions.add(submission);
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public void addMentor(Mentor mentor) {
        mentors.add(mentor);
    }

    public void removeMentor(Mentor mentor) {
        mentors.remove(mentor);
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public void addSupportRequest(SupportRequest request) {
        supportRequests.add(request);
    }

    public void removeSupportRequest(SupportRequest request) {
        supportRequests.remove(request);
    }

    public void publishLeaderboard() {

        if (leaderboardPublished) {
            throw new IllegalStateException("Leaderboard has already been published.");
        }

        leaderboardPublished = true;
        state = HackathonState.COMPLETED;
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public boolean hasJudge() {
        return judge != null;
    }

    public boolean isRegistrationOpen() {
        return state == HackathonState.REGISTRATION_OPEN;
    }

    public boolean isInProgress() {
        return state == HackathonState.IN_PROGRESS;
    }

    public boolean isUnderEvaluation() {
        return state == HackathonState.UNDER_EVALUATION;
    }

    public boolean isCompleted() {
        return state == HackathonState.COMPLETED;
    }

    public boolean isLeaderboardPublished() {
        return leaderboardPublished;
    }

    public List<Team> getLeaderboard() {

        List<Team> leaderboard = new ArrayList<>();

        if (winner != null) {
            leaderboard.add(winner);
        }

        return leaderboard;
    }

    public Long getHackathonId() {
        return hackathonId;
    }

    public String getName() {
        return name;
    }

    public String getRegulation() {
        return regulation;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getPrize() {
        return prize;
    }
    public int getMaxTeamMembers() {
        return maxTeamMembers;
    }

    public HackathonState getState() {
        return state;
    }

    public Judge getJudge() {
        return judge;
    }

    public Team getWinner() {
        return winner;
    }

    public List<Mentor> getMentors() {
        return Collections.unmodifiableList(mentors);
    }

    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(registrations);
    }

    public List<Submission> getSubmissions() {
        return Collections.unmodifiableList(submissions);
    }

    public List<SupportRequest> getSupportRequests() {
        return Collections.unmodifiableList(supportRequests);
    }

    /*==================================================
                    SETTERS (used by Builder)
    ==================================================*/

    public void setHackathonId(Long hackathonId) {
        this.hackathonId = hackathonId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }

    public void setMaxTeamMembers(int maxTeamMembers) {
        this.maxTeamMembers = maxTeamMembers;
    }

}