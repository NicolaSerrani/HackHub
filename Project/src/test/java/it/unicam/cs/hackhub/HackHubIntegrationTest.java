package it.unicam.cs.hackhub;

import it.unicam.cs.hackhub.model.entity.*;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.RegistrationRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import it.unicam.cs.hackhub.service.HackathonService;
import it.unicam.cs.hackhub.service.EvaluationService;
import it.unicam.cs.hackhub.service.TeamService;
import it.unicam.cs.hackhub.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class HackHubIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private TeamService teamService;
    @Autowired private HackathonService hackathonService;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private HackathonRepository hackathonRepository;
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private EvaluationService evaluationService;

    @AfterEach
    void clearSession() {
        try {
            userService.logout();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    void persistsCoreUseCaseInH2() {
        User member = userService.register("Mario Rossi", "mario@example.test", "password123", "USER");
        User organizer = userService.register("Anna Neri", "anna@example.test", "password123", "STAFF");
        userService.login(member.getEmail(), "password123");
        Team team = teamService.createTeam("Test Builders");
        assertThatThrownBy(() -> hackathonService.createHackathon(new HackathonBuilder()
                .setName("Non autorizzato")
                .setRegulation("Regolamento")
                .setLocation("Camerino")
                .setRegistrationDeadline(LocalDate.now().plusDays(2))
                .setStartDate(LocalDate.now().plusDays(3))
                .setEndDate(LocalDate.now().plusDays(5))
                .setPrize(1000)
                .setMaxTeamMembers(5))).isInstanceOf(IllegalStateException.class);
        userService.logout(member.getUserId());

        userService.login(organizer.getEmail(), "password123");
        Hackathon hackathon = hackathonService.createHackathon(new HackathonBuilder()
                .setName("HackHub Test")
                .setRegulation("Regolamento di test")
                .setLocation("Camerino")
                .setRegistrationDeadline(LocalDate.now().plusDays(2))
                .setStartDate(LocalDate.now().plusDays(3))
                .setEndDate(LocalDate.now().plusDays(5))
                .setPrize(1000)
                .setMaxTeamMembers(5));
        userService.logout(organizer.getUserId());

        userService.login(member.getEmail(), "password123");
        hackathonService.registerTeam(hackathon.getHackathonId(), team.getTeamId());
        userService.logout(member.getUserId());

        assertThat(userRepository.findById(member.getUserId())).isPresent();
        assertThat(teamRepository.findById(team.getTeamId())).isPresent();
        assertThat(hackathonRepository.findById(hackathon.getHackathonId())).isPresent();
        assertThat(hackathon.getOrganizer().getUserId()).isEqualTo(organizer.getUserId());
        assertThat(registrationRepository.existsByHackathon_HackathonIdAndTeam_TeamId(
                hackathon.getHackathonId(), team.getTeamId())).isTrue();
    }

    @Test
    void acceptsOnlyRegistrationRolesAndSeedsContextualRoles() {
        assertThatThrownBy(() -> userService.register(
                "Wrong Role", "wrong@example.test", "password123", "MENTOR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role must be USER or STAFF");

        User marco = userRepository.findByEmailIgnoreCase("marco.rossi@hackhub.local").orElseThrow();
        Mentor elena = (Mentor) userRepository.findByEmailIgnoreCase("elena.conti@hackhub.local").orElseThrow();
        Judge luca = (Judge) userRepository.findByEmailIgnoreCase("luca.ferrari@hackhub.local").orElseThrow();
        Organizer sofia = (Organizer) userRepository.findByEmailIgnoreCase("sofia.romano@hackhub.local").orElseThrow();
        Hackathon demo = hackathonRepository.findById(1L).orElseThrow();

        assertThat(marco).isInstanceOf(TeamMember.class);
        assertThat(marco.getRole()).isEqualTo("USER");
        assertThat(marco.getTeam()).isNotNull();
        assertThat(elena).isInstanceOf(Mentor.class);
        assertThat(elena.getRole()).isEqualTo("STAFF");
        assertThat(luca).isInstanceOf(Judge.class);
        assertThat(sofia).isInstanceOf(Organizer.class);
        assertThat(demo.getMentors()).contains(elena);
        assertThat(demo.getJudge()).isEqualTo(luca);
        assertThat(demo.getOrganizer()).isEqualTo(sofia);
    }

    @Test
    void preventsAnotherLoginUntilLogout() {
        User first = userService.register("Primo Utente", "first@example.test", "password123");
        User second = userService.register("Secondo Utente", "second@example.test", "password123");

        userService.login(first.getEmail(), "password123");
        assertThatThrownBy(() -> userService.login(second.getEmail(), "password123"))
                .isInstanceOf(IllegalStateException.class);

        userService.logout(first.getUserId());
        assertThat(userService.login(second.getEmail(), "password123").getUserId())
                .isEqualTo(second.getUserId());
        userService.logout();
    }

    @Test
    void assignedJudgeEvaluatesSubmissionOnlyOnce() {
        userService.login("luca.ferrari@hackhub.local", "password123");

        assertThatThrownBy(() -> evaluationService.evaluateSubmission(2L, 4L, 8, "Bozza"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only submitted projects can be evaluated.");
        assertThat(evaluationService.evaluateSubmission(1L, 4L, 8.5, "Valutazione di test").getEvaluationId())
                .isNotNull();
        assertThatThrownBy(() -> evaluationService.evaluateSubmission(1L, 4L, 7, "Duplicata"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Submission has already been evaluated");

        userService.logout();
    }
}
