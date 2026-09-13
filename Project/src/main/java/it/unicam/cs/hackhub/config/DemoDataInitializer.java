package it.unicam.cs.hackhub.config;

import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.repository.UserRepository;
import it.unicam.cs.hackhub.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DemoDataInitializer implements CommandLineRunner {
    private final UserService users;
    private final UserRepository userRepository;
    private final HackathonService hackathons;
    private final TeamService teams;
    private final InvitationService invitations;
    private final SubmissionService submissions;
    private final SupportRequestService supportRequests;
    private final EvaluationService evaluations;

    public DemoDataInitializer(UserService users, UserRepository userRepository, HackathonService hackathons,
                               TeamService teams, InvitationService invitations, SubmissionService submissions,
                               SupportRequestService supportRequests, EvaluationService evaluations) {
        this.users = users;
        this.userRepository = userRepository;
        this.hackathons = hackathons;
        this.teams = teams;
        this.invitations = invitations;
        this.submissions = submissions;
        this.supportRequests = supportRequests;
        this.evaluations = evaluations;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        if (userRepository.findByEmailIgnoreCase("marco.rossi@hackhub.local").orElseThrow().hasTeam()) return;

        users.login("sofia.romano@hackhub.local", "password123");
        var demo = hackathons.createHackathon(hackathon("HackHub Demo", "Camerino", 5, 5, 5000));
        var mentorInvitation = hackathons.addMentor(demo.getHackathonId(), 3L);
        var judgeInvitation = hackathons.addJudge(demo.getHackathonId(), 4L);
        users.logout();

        users.login("elena.conti@hackhub.local", "password123");
        invitations.acceptMentorInvitation(mentorInvitation.getInvitationId());
        users.logout();
        users.login("luca.ferrari@hackhub.local", "password123");
        invitations.acceptJudgeInvitation(judgeInvitation.getInvitationId());
        users.logout();

        users.login("marco.rossi@hackhub.local", "password123");
        var team = teams.createTeam("Byte Builders");
        hackathons.registerTeam(demo.getHackathonId(), team.getTeamId());
        teams.inviteUsers(team.getTeamId(), List.of(8L));
        users.logout();

        users.login("sofia.romano@hackhub.local", "password123");
        hackathons.addMentorToTeam(team.getTeamId(), 3L);
        var future = hackathons.createHackathon(hackathon("Future Tech Challenge", "Ancona", 4, 6, 3000));
        hackathons.addMentor(future.getHackathonId(), 6L);
        hackathons.addJudge(future.getHackathonId(), 7L);
        hackathons.createHackathon(hackathon("Open Innovation Day", "Macerata", 4, 7, 2000));
        users.logout();

        users.login("marco.rossi@hackhub.local", "password123");
        var submission = submissions.saveDraft(team.getTeamId(), demo.getHackathonId(), "EcoTrack",
                "Monitoraggio dei consumi", "https://github.com/example/ecotrack");
        submissions.submitSubmission(submission.getSubmissionId());
        submissions.saveDraft(team.getTeamId(), demo.getHackathonId(), "Bozza da inviare",
                "Dati predisposti per il caso d'uso di invio", "https://github.com/example/to-submit");
        submissions.saveDraft(team.getTeamId(), demo.getHackathonId(), "Bozza da aggiornare",
                "Dati predisposti per il caso d'uso di aggiornamento", "https://github.com/example/to-update");
        supportRequests.createSupportRequest(team.getTeamId(), 3L, demo.getHackathonId(),
                "Problema tecnico", "Serve supporto per il deploy");
        users.logout();

        users.login("sofia.romano@hackhub.local", "password123");
        var finals = hackathons.createHackathon(hackathon("Finals Arena", "Ascoli Piceno", 5, 8, 4000));
        var finalsJudgeInvitation = hackathons.addJudge(finals.getHackathonId(), 4L);
        users.logout();
        users.login("luca.ferrari@hackhub.local", "password123");
        invitations.acceptJudgeInvitation(finalsJudgeInvitation.getInvitationId());
        users.logout();
        users.login("marco.rossi@hackhub.local", "password123");
        hackathons.registerTeam(finals.getHackathonId(), team.getTeamId());
        var finalist = submissions.saveDraft(team.getTeamId(), finals.getHackathonId(), "Final Project",
                "Submission già valutata per i test organizer", "https://github.com/example/final-project");
        submissions.submitSubmission(finalist.getSubmissionId());
        users.logout();
        users.login("sofia.romano@hackhub.local", "password123");
        hackathons.changeState(finals.getHackathonId(), it.unicam.cs.hackhub.model.enumeration.HackathonState.UNDER_EVALUATION);
        users.logout();
        users.login("luca.ferrari@hackhub.local", "password123");
        evaluations.evaluateSubmission(finalist.getSubmissionId(), 4L, 9, "Submission finale verificata");
        users.logout();
    }

    private void seedUsers() {
        String[][] data = {
                {"Giulia Bianchi", "giulia.bianchi@hackhub.local", "USER"},
                {"Marco Rossi", "marco.rossi@hackhub.local", "USER"},
                {"Elena Conti", "elena.conti@hackhub.local", "STAFF"},
                {"Luca Ferrari", "luca.ferrari@hackhub.local", "STAFF"},
                {"Sofia Romano", "sofia.romano@hackhub.local", "STAFF"},
                {"Marta Ricci", "marta.ricci@hackhub.local", "STAFF"},
                {"Paolo Greco", "paolo.greco@hackhub.local", "STAFF"},
                {"Anna Verdi", "anna.verdi@hackhub.local", "USER"},
                {"Carlo Blu", "carlo.blu@hackhub.local", "STAFF"},
                {"Davide Gialli", "davide.gialli@hackhub.local", "USER"}
        };
        for (String[] user : data) {
            if (!userRepository.existsByEmailIgnoreCase(user[1])) {
                users.register(user[0], user[1], "password123", user[2]);
            }
        }
    }

    private HackathonBuilder hackathon(String name, String location, int maxMembers, int month, double prize) {
        return new HackathonBuilder().setName(name).setRegulation("Regolamento " + name).setLocation(location)
                .setRegistrationDeadline(LocalDate.of(2027, month, 1))
                .setStartDate(LocalDate.of(2027, month, 10))
                .setEndDate(LocalDate.of(2027, month, 12)).setPrize(prize).setMaxTeamMembers(maxMembers);
    }
}
