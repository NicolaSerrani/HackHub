package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.integration.payment.PaymentSystem;
import it.unicam.cs.hackhub.model.entity.*;
import it.unicam.cs.hackhub.model.enumeration.HackathonState;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HackathonService {
    private final HackathonRepository hackathons;
    private final TeamRepository teams;
    private final UserRepository users;
    private final RegistrationRepository registrations;
    private final InvitationRepository invitations;
    private final PaymentRepository payments;
    private final PaymentSystem paymentSystem;
    private final UserService userService;

    public HackathonService(HackathonRepository hackathons, TeamRepository teams, UserRepository users,
                            RegistrationRepository registrations, InvitationRepository invitations,
                            PaymentRepository payments, PaymentSystem paymentSystem, UserService userService) {
        this.hackathons = hackathons;
        this.teams = teams;
        this.users = users;
        this.registrations = registrations;
        this.invitations = invitations;
        this.payments = payments;
        this.paymentSystem = paymentSystem;
        this.userService = userService;
    }

    public Hackathon createHackathon(HackathonBuilder builder) {
        if (builder == null) throw new IllegalArgumentException("Hackathon builder cannot be null");
        Hackathon hackathon = builder.build();
        validate(hackathon);
        Organizer organizer = userService.acquireOrganizer();
        hackathon.setOrganizer(organizer);
        return hackathons.save(hackathon);
    }

    public Registration registerTeam(Long hackathonId, Long teamId) {
        requireOwnTeam(teamId);
        Hackathon hackathon = findHackathon(hackathonId);
        if (registrations.existsByHackathon_HackathonIdAndTeam_TeamId(hackathonId, teamId))
            throw new IllegalStateException("Team is already registered");
        hackathon.registerTeam(findTeam(teamId));
        return registrations.saveAndFlush(hackathon.getRegistrations().getLast());
    }

    public Invitation addMentor(Long hackathonId, Long mentorId) {
        return inviteStaff(findHackathon(hackathonId), mentorId, InvitationType.MENTOR);
    }

    public Invitation addJudge(Long hackathonId, Long judgeId) {
        Hackathon hackathon = findHackathon(hackathonId);
        if (hackathon.getJudge() != null) throw new IllegalStateException("A judge is already assigned");
        return inviteStaff(hackathon, judgeId, InvitationType.JUDGE);
    }

    public Team addMentorToTeam(Long teamId, Long mentorId) {
        Team team = findTeam(teamId);
        Hackathon hackathon = requireOrganizedHackathonForTeam(teamId);
        User selected = findStaff(mentorId);
        if (!(selected instanceof Mentor mentor)) {
            throw new IllegalStateException("The staff member has not accepted a mentor invitation");
        }
        if (!hackathon.getMentors().contains(mentor))
            throw new IllegalStateException("The staff member is not a mentor for this hackathon");
        if (team.getSupportMentor() != null && !team.getSupportMentor().equals(mentor))
            throw new IllegalStateException("Team already has a support mentor");
        team.setSupportMentor(mentor);
        return teams.save(team);
    }

    public Hackathon changeState(Long hackathonId, HackathonState state) {
        Hackathon hackathon = findHackathon(hackathonId);
        requireOrganizer(hackathon);
        hackathon.setState(state);
        return hackathons.save(hackathon);
    }

    public Hackathon declareWinner(Long hackathonId, Long teamId) {
        Hackathon hackathon = findHackathon(hackathonId);
        requireOrganizer(hackathon);
        Team team = registrations.findByHackathon_HackathonId(hackathonId).stream()
                .filter(Registration::isActive).map(Registration::getTeam)
                .filter(candidate -> teamId.equals(candidate.getTeamId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team is not registered for this hackathon"));
        if (hackathon.getState() != HackathonState.UNDER_EVALUATION)
            throw new IllegalStateException("Winner can only be declared during evaluation");
        if (hackathon.getSubmissions().isEmpty()
                || hackathon.getSubmissions().stream().anyMatch(s -> s.getEvaluation() == null))
            throw new IllegalStateException("All submissions must be evaluated first");
        hackathon.setWinner(team);
        return hackathons.save(hackathon);
    }

    public Hackathon publishLeaderboard(Long hackathonId) {
        Hackathon hackathon = findHackathon(hackathonId);
        requireOrganizer(hackathon);
        if (hackathon.isLeaderboardPublished()) throw new IllegalStateException("Leaderboard already published");
        hackathon.publishLeaderboard();
        return hackathons.save(hackathon);
    }

    @Transactional(readOnly = true)
    public List<String> getLeaderboard(Long hackathonId) {
        userService.requireAuthenticated();
        Hackathon hackathon = findHackathon(hackathonId);
        if (hackathon.getState() != HackathonState.COMPLETED || !hackathon.isLeaderboardPublished()) {
            throw new IllegalStateException("The final leaderboard is not available yet");
        }
        return hackathon.getLeaderboard().stream().map(Team::getName).toList();
    }

    public Payment awardPrize(Long hackathonId) {
        Hackathon hackathon = findHackathon(hackathonId);
        requireOrganizer(hackathon);
        if (hackathon.getState() != HackathonState.COMPLETED)
            throw new IllegalStateException("The prize can be awarded only after completing the hackathon");
        if (payments.findByHackathon_HackathonId(hackathonId).isPresent())
            throw new IllegalStateException("The prize payment has already been created");
        Payment payment = hackathon.awardPrize();
        payment.execute();
        if (paymentSystem.requestPayment(payment)) payment.confirm();
        else {
            payment.reject();
            payments.save(payment);
            throw new IllegalStateException("The payment system rejected the prize payment");
        }
        return payments.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Hackathon> viewHackathons() { return hackathons.findAll(); }

    @Transactional(readOnly = true)
    public Hackathon getHackathonDetails(Long hackathonId) { return findHackathon(hackathonId); }

    @Transactional(readOnly = true)
    public List<Team> viewRegisteredTeams(Long hackathonId) {
        requireOrganizer(findHackathon(hackathonId));
        return registrations.findActiveTeams(hackathonId);
    }

    @Transactional(readOnly = true)
    public List<Mentor> viewAvailableMentors(Long teamId) {
        Team team = findTeam(teamId);
        Hackathon hackathon = requireOrganizedHackathonForTeam(teamId);
        return team.getSupportMentor() == null ? hackathon.getMentors() : List.of();
    }

    private Invitation inviteStaff(Hackathon hackathon, Long staffId, InvitationType type) {
        Organizer organizer = requireOrganizer(hackathon);
        StaffMember staff = findStaff(staffId);
        if (type == InvitationType.MENTOR && hackathon.getMentors().contains(staff)) {
            throw new IllegalStateException("The staff member is already a mentor for this hackathon");
        }
        if (staff.getClass() != StaffMember.class
                && !(type == InvitationType.MENTOR && staff instanceof Mentor)
                && !(type == InvitationType.JUDGE && staff instanceof Judge)) {
            throw new IllegalStateException("The staff member already has a different contextual role");
        }
        boolean pending = invitations.findByReceiver_UserId(staffId).stream()
                .anyMatch(i -> i.isPending() && i.getType() == type && hackathon.equals(i.getHackathon()));
        if (pending) throw new IllegalStateException("A pending invitation already exists");
        Invitation invitation = new Invitation();
        invitation.setType(type);
        invitation.setSender(organizer);
        invitation.setReceiver(staff);
        invitation.setHackathon(hackathon);
        staff.addInvitation(invitation);
        return invitations.save(invitation);
    }

    private Organizer requireOrganizer(Hackathon hackathon) {
        Organizer staff = userService.requireOrganizer();
        if (hackathon.getOrganizer() == null || !hackathon.getOrganizer().getUserId().equals(staff.getUserId()))
            throw new IllegalStateException("This operation requires the hackathon organizer");
        return staff;
    }

    private Hackathon requireOrganizedHackathonForTeam(Long teamId) {
        Organizer staff = userService.requireOrganizer();
        return registrations.findByTeam_TeamId(teamId).stream().filter(Registration::isActive)
                .map(Registration::getHackathon)
                .filter(h -> h.getOrganizer().getUserId().equals(staff.getUserId()))
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        "This operation requires the organizer of the team's hackathon"));
    }

    private Hackathon findHackathon(Long id) {
        return hackathons.findById(id).orElseThrow(() -> new IllegalArgumentException("Hackathon not found: " + id));
    }

    private Team findTeam(Long id) {
        return teams.findById(id).orElseThrow(() -> new IllegalArgumentException("Team not found: " + id));
    }

    private StaffMember findStaff(Long id) {
        User user = users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found: " + id));
        if (!(user instanceof StaffMember staff)) {
            throw new IllegalArgumentException("Staff member not found: " + id);
        }
        return staff;
    }

    private TeamMember requireOwnTeam(Long teamId) {
        TeamMember member = userService.requireTeamMember();
        if (!member.getTeam().getTeamId().equals(teamId))
            throw new IllegalStateException("A team member can register only their own team.");
        return member;
    }

    private void validate(Hackathon h) {
        boolean dataValid = h.getName() != null && !h.getName().isBlank()
                && h.getRegulation() != null && !h.getRegulation().isBlank()
                && h.getLocation() != null && !h.getLocation().isBlank()
                && h.getMaxTeamMembers() > 0 && h.getPrize() != null && h.getPrize() >= 0;
        boolean datesValid = h.getRegistrationDeadline() != null && h.getStartDate() != null && h.getEndDate() != null
                && !h.getRegistrationDeadline().isAfter(h.getStartDate()) && h.getStartDate().isBefore(h.getEndDate());
        if (!dataValid || !datesValid) throw new IllegalArgumentException("Invalid hackathon data");
    }
}
