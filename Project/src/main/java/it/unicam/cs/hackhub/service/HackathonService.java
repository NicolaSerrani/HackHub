package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.integration.payment.PaymentSystem;
import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Payment;
import it.unicam.cs.hackhub.model.entity.Registration;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.enumeration.HackathonState;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.PaymentRepository;
import it.unicam.cs.hackhub.repository.RegistrationRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HackathonService {
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentSystem paymentSystem;

    public HackathonService(HackathonRepository hackathonRepository, TeamRepository teamRepository,
                            UserRepository userRepository, RegistrationRepository registrationRepository,
                            PaymentRepository paymentRepository, PaymentSystem paymentSystem) {
        this.hackathonRepository = hackathonRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.paymentRepository = paymentRepository;
        this.paymentSystem = paymentSystem;
    }

    public Hackathon createHackathon(HackathonBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Hackathon builder cannot be null");
        }
        Hackathon hackathon = builder.build();
        validate(hackathon);
        return hackathonRepository.save(hackathon);
    }

    public Registration registerTeam(Long hackathonId, Long teamId) {
        Hackathon hackathon = findHackathon(hackathonId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        if (registrationRepository.existsByHackathon_HackathonIdAndTeam_TeamId(hackathonId, teamId)) {
            throw new IllegalStateException("Team is already registered");
        }
        hackathon.registerTeam(team);
        Registration registration = hackathon.getRegistrations().getLast();
        return registrationRepository.saveAndFlush(registration);
    }

    public Hackathon addMentor(Long hackathonId, Long mentorId) {
        Hackathon hackathon = findHackathon(hackathonId);
        Mentor mentor = findMentor(mentorId);
        hackathon.addMentor(mentor);
        return hackathonRepository.save(hackathon);
    }

    public Team addMentorToTeam(Long teamId, Long mentorId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        findMentor(mentorId).supportTeam(team);
        return teamRepository.save(team);
    }

    public Hackathon addJudge(Long hackathonId, Long judgeId) {
        Hackathon hackathon = findHackathon(hackathonId);
        Judge judge = userRepository.findById(judgeId).filter(Judge.class::isInstance).map(Judge.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Judge not found: " + judgeId));
        hackathon.setJudge(judge);
        return hackathonRepository.save(hackathon);
    }

    public Hackathon changeState(Long hackathonId, HackathonState state) {
        Hackathon hackathon = findHackathon(hackathonId);
        hackathon.setState(state);
        return hackathonRepository.save(hackathon);
    }

    public Hackathon declareWinner(Long hackathonId, Long teamId) {
        Hackathon hackathon = findHackathon(hackathonId);
        Team team = registrationRepository.findByHackathon_HackathonId(hackathonId).stream()
                .filter(Registration::isActive).map(Registration::getTeam)
                .filter(candidate -> teamId.equals(candidate.getTeamId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team is not registered for this hackathon"));
        if (hackathon.getState() != HackathonState.UNDER_EVALUATION) {
            throw new IllegalStateException("Winner can only be declared during evaluation");
        }
        if (hackathon.getSubmissions().isEmpty()
                || hackathon.getSubmissions().stream().anyMatch(submission -> submission.getEvaluation() == null)) {
            throw new IllegalStateException("All submissions must be evaluated first");
        }
        hackathon.setWinner(team);
        return hackathonRepository.save(hackathon);
    }

    public Hackathon publishLeaderboard(Long hackathonId) {
        Hackathon hackathon = findHackathon(hackathonId);
        if (hackathon.isLeaderboardPublished()) {
            throw new IllegalStateException("Leaderboard has already been published");
        }
        hackathon.publishLeaderboard();
        return hackathonRepository.save(hackathon);
    }

    @Transactional(readOnly = true)
    public List<Team> getLeaderboard(Long hackathonId) {
        return findHackathon(hackathonId).getLeaderboard();
    }

    public Payment awardPrize(Long hackathonId) {
        Hackathon hackathon = findHackathon(hackathonId);
        if (hackathon.getState() != HackathonState.COMPLETED) {
            throw new IllegalStateException("The prize can be awarded only after completing the hackathon");
        }
        if (paymentRepository.findByHackathon_HackathonId(hackathonId).isPresent()) {
            throw new IllegalStateException("The prize payment has already been created");
        }
        Payment payment = hackathon.awardPrize();
        payment.execute();
        if (paymentSystem.requestPayment(payment)) {
            payment.confirm();
        } else {
            payment.reject();
            paymentRepository.save(payment);
            throw new IllegalStateException("The payment system rejected the prize payment");
        }
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Hackathon> viewHackathons() {
        return hackathonRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Hackathon getHackathonDetails(Long hackathonId) {
        return findHackathon(hackathonId);
    }

    @Transactional(readOnly = true)
    public List<Team> viewRegisteredTeams(Long hackathonId) {
        return registrationRepository.findActiveTeams(hackathonId);
    }

    @Transactional(readOnly = true)
    public List<Mentor> viewAvailableMentors(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        if (team.getSupportMentor() != null) {
            return List.of();
        }
        return userRepository.findAllMentors().stream()
                .filter(mentor -> !mentor.getSupportedTeams().contains(team)).toList();
    }

    private Hackathon findHackathon(Long hackathonId) {
        return hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found: " + hackathonId));
    }

    private Mentor findMentor(Long mentorId) {
        return userRepository.findById(mentorId).filter(Mentor.class::isInstance).map(Mentor.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found: " + mentorId));
    }

    private void validate(Hackathon hackathon) {
        boolean dataValid = hackathon.getName() != null && !hackathon.getName().isBlank()
                && hackathon.getRegulation() != null && !hackathon.getRegulation().isBlank()
                && hackathon.getLocation() != null && !hackathon.getLocation().isBlank()
                && hackathon.getMaxTeamMembers() > 0 && hackathon.getPrize() != null && hackathon.getPrize() >= 0;
        boolean datesValid = hackathon.getRegistrationDeadline() != null && hackathon.getStartDate() != null
                && hackathon.getEndDate() != null
                && !hackathon.getRegistrationDeadline().isAfter(hackathon.getStartDate())
                && hackathon.getStartDate().isBefore(hackathon.getEndDate());
        if (!dataValid || !datesValid) {
            throw new IllegalArgumentException("Invalid hackathon data");
        }
    }
}
