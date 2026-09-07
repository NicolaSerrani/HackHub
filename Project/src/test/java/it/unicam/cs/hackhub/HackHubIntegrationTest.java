package it.unicam.cs.hackhub;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.RegistrationRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import it.unicam.cs.hackhub.service.HackathonService;
import it.unicam.cs.hackhub.service.TeamService;
import it.unicam.cs.hackhub.service.UserService;
import org.junit.jupiter.api.Test;
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

    @Test
    void persistsCoreUseCaseInH2() {
        User member = userService.register("Mario Rossi", "mario@example.test", "password123", "TEAM_MEMBER");
        Team team = teamService.createTeam("Byte Builders");
        Hackathon hackathon = hackathonService.createHackathon(new HackathonBuilder()
                .setName("HackHub Test")
                .setRegulation("Regolamento di test")
                .setLocation("Camerino")
                .setRegistrationDeadline(LocalDate.now().plusDays(2))
                .setStartDate(LocalDate.now().plusDays(3))
                .setEndDate(LocalDate.now().plusDays(5))
                .setPrize(1000)
                .setMaxTeamMembers(5));

        hackathonService.registerTeam(hackathon.getHackathonId(), team.getTeamId());

        assertThat(userRepository.findById(member.getUserId())).isPresent();
        assertThat(teamRepository.findById(team.getTeamId())).isPresent();
        assertThat(hackathonRepository.findById(hackathon.getHackathonId())).isPresent();
        assertThat(registrationRepository.existsByHackathon_HackathonIdAndTeam_TeamId(
                hackathon.getHackathonId(), team.getTeamId())).isTrue();
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
        userService.logout(second.getUserId());
    }
}
