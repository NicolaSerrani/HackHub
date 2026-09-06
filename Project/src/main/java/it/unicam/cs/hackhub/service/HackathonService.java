package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.Payment;
import it.unicam.cs.hackhub.model.enumeration.HackathonState;
import it.unicam.cs.hackhub.integration.payment.LocalPaymentSystem;
import it.unicam.cs.hackhub.integration.payment.PaymentSystem;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.repository.PaymentRepository;
import it.unicam.cs.hackhub.repository.memory.InMemoryPaymentRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HackathonService {

    private static final Map<Long, Hackathon> hackathons = new LinkedHashMap<>();
    private static final Map<Long, Mentor> mentors = new LinkedHashMap<>();
    private static final Map<Long, Judge> judges = new LinkedHashMap<>();
    private static long nextHackathonId = 1;
    private static long nextPaymentId = 1;
    private final PaymentSystem paymentSystem;
    private final PaymentRepository paymentRepository;
    private Hackathon selectedHackathon;
    private Team selectedTeam;

    public HackathonService() {
        this(new LocalPaymentSystem(), new InMemoryPaymentRepository());
    }

    public HackathonService(PaymentSystem paymentSystem) {
        this(paymentSystem, new InMemoryPaymentRepository());
    }

    public HackathonService(PaymentSystem paymentSystem, PaymentRepository paymentRepository) {
        if (paymentSystem == null) {
            throw new IllegalArgumentException("Payment system cannot be null");
        }
        if (paymentRepository == null) {
            throw new IllegalArgumentException("Payment repository cannot be null");
        }
        this.paymentSystem = paymentSystem;
        this.paymentRepository = paymentRepository;
    }

    public void createHackathon(HackathonBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Hackathon builder cannot be null");
        }
        selectedHackathon = builder.build();
        if (!validateHackathonData() || !validateDates() || !validateStaffSelection()) {
            throw new IllegalArgumentException("Invalid hackathon data");
        }
        selectedHackathon.setHackathonId(nextHackathonId++);
        hackathons.put(selectedHackathon.getHackathonId(), selectedHackathon);
    }

    public void registerTeam(Long hackathonId) {
        Team currentTeam = TeamService.getCurrentTeam();
        if (currentTeam == null) {
            throw new IllegalStateException("No current team is available");
        }
        registerTeam(hackathonId, currentTeam.getTeamId());
    }

    public void registerTeam(Long hackathonId, Long teamId) {
        selectedHackathon = findHackathon(hackathonId);
        selectedTeam = TeamService.findTeam(teamId);
        if (!checkAvailability(hackathonId) || !checkTeamRequirements()
                || !checkTeamRegistration() || checkAlreadyRegisteredTeam()) {
            throw new IllegalStateException("Team cannot be registered");
        }
        selectedHackathon.registerTeam(selectedTeam);
    }

    public void addMentor(Long hackathonId, Long mentorId) {
        selectedHackathon = findHackathon(hackathonId);
        if (!checkAvailability(hackathonId) || mentorId == null) {
            throw new IllegalArgumentException("Hackathon or mentor is not available");
        }
        selectedHackathon.addMentor(mentors.computeIfAbsent(mentorId, id -> new Mentor()));
    }

    public void addMentorToTeam(Long teamId, Long mentorId) {
        Team team = TeamService.findTeam(teamId);
        Mentor mentor = mentors.get(mentorId);
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor not found: " + mentorId);
        }
        if (team.getSupportMentor() != null) {
            throw new IllegalStateException("Team already has a mentor");
        }
        mentor.supportTeam(team);
    }

    public void addJudge(Long hackathonId, Long judgeId) {
        selectedHackathon = findHackathon(hackathonId);
        if (!checkAvailability(hackathonId) || judgeId == null) {
            throw new IllegalArgumentException("Hackathon or judge is not available");
        }
        selectedHackathon.setJudge(judges.computeIfAbsent(judgeId, id -> new Judge()));
    }

    public void declareWinner(Long hackathonId, Long teamId) {
        selectedHackathon = findHackathon(hackathonId);
        selectedTeam = selectedHackathon.getRegistrations().stream()
                .filter(registration -> registration.isActive())
                .map(registration -> registration.getTeam())
                .filter(team -> teamId != null && teamId.equals(team.getTeamId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team is not registered for this hackathon"));
        if (!checkWinnerSelection() || !checkAllSubmissionsEvaluated()
                || !checkWinnerBelongsToHackathon()) {
            throw new IllegalStateException("Winner cannot be declared");
        }
        selectedHackathon.setWinner(selectedTeam);
    }

    public void publishLeaderboard(Long hackathonId) {
        selectedHackathon = findHackathon(hackathonId);
        if (!checkLeaderboardPublished() || !checkHackathonCompleted()) {
            throw new IllegalStateException("Leaderboard cannot be published");
        }
        selectedHackathon.publishLeaderboard();
    }

    public List<Team> getLeaderboard(Long hackathonId) {
        return findHackathon(hackathonId).getLeaderboard();
    }

    public void awardPrize(Long hackathonId) {
        selectedHackathon = findHackathon(hackathonId);
        if (selectedHackathon.getState() != HackathonState.COMPLETED) {
            throw new IllegalStateException("The prize can be awarded only after completing the hackathon");
        }

        Payment payment = selectedHackathon.awardPrize();
        payment.setPaymentId(nextPaymentId++);
        payment.execute();
        if (paymentSystem.requestPayment(payment)) {
            payment.confirm();
        } else {
            payment.reject();
            paymentRepository.save(payment);
            throw new IllegalStateException("The payment system rejected the prize payment");
        }
        paymentRepository.save(payment);
    }

    public List<Hackathon> viewHackathons() {
        return new ArrayList<>(hackathons.values());
    }

    public Hackathon getHackathonDetails(Long hackathonId) {
        return findHackathon(hackathonId).getDetails();
    }

    public List<Team> viewRegisteredTeams(Long hackathonId) {
        return findHackathon(hackathonId).getRegistrations().stream()
                .filter(registration -> registration.isActive())
                .map(registration -> registration.getTeam())
                .toList();
    }

    public List<Mentor> viewAvailableMentors(Long teamId) {
        Team team = TeamService.findTeam(teamId);
        if (team.getSupportMentor() != null) {
            return List.of();
        }
        return mentors.values().stream()
                .filter(mentor -> !mentor.getSupportedTeams().contains(team))
                .toList();
    }

    static Hackathon findHackathon(Long hackathonId) {
        Hackathon hackathon = hackathons.get(hackathonId);
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon not found: " + hackathonId);
        }
        return hackathon;
    }

    private boolean validateHackathonData() {
        return selectedHackathon.getName() != null && !selectedHackathon.getName().isBlank()
                && selectedHackathon.getRegulation() != null && !selectedHackathon.getRegulation().isBlank()
                && selectedHackathon.getLocation() != null && !selectedHackathon.getLocation().isBlank()
                && selectedHackathon.getMaxTeamMembers() > 0
                && selectedHackathon.getPrize() != null && selectedHackathon.getPrize() >= 0;
    }

    private boolean validateDates() {
        return selectedHackathon.getRegistrationDeadline() != null
                && selectedHackathon.getStartDate() != null
                && selectedHackathon.getEndDate() != null
                && !selectedHackathon.getRegistrationDeadline().isAfter(selectedHackathon.getStartDate())
                && selectedHackathon.getStartDate().isBefore(selectedHackathon.getEndDate());
    }

    private boolean validateStaffSelection() {
        return true;
    }

    private boolean checkTeamRequirements() {
        return selectedTeam != null && selectedTeam.getMemberCount() <= selectedHackathon.getMaxTeamMembers();
    }

    private boolean checkAlreadyRegisteredTeam() {
        return selectedHackathon.getRegistrations().stream()
                .anyMatch(registration -> registration.isActive() && registration.getTeam() == selectedTeam);
    }

    private boolean checkTeamRegistration() {
        return selectedHackathon.getState() == HackathonState.REGISTRATION_OPEN;
    }

    private boolean checkWinnerSelection() {
        return selectedHackathon.getState() == HackathonState.UNDER_EVALUATION;
    }

    private boolean checkAllSubmissionsEvaluated() {
        return !selectedHackathon.getSubmissions().isEmpty()
                && selectedHackathon.getSubmissions().stream()
                .allMatch(submission -> submission.getEvaluation() != null);
    }

    private boolean checkWinnerBelongsToHackathon() {
        return selectedHackathon.getRegistrations().stream()
                .anyMatch(registration -> registration.isActive() && registration.getTeam() == selectedTeam);
    }

    private boolean checkLeaderboardPublished() {
        return !selectedHackathon.isLeaderboardPublished();
    }

    private boolean checkHackathonCompleted() {
        return selectedHackathon.getState() == HackathonState.UNDER_EVALUATION
                && selectedHackathon.getWinner() != null;
    }

    private boolean checkExistence() {
        return selectedHackathon != null;
    }

    private boolean checkAvailability(Long hackathonId) {
        return checkExistence() && hackathonId != null
                && hackathonId.equals(selectedHackathon.getHackathonId());
    }
}
