package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.Payment;
import it.unicam.cs.hackhub.model.entity.Registration;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.enumeration.HackathonState;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.service.HackathonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {
    private final HackathonService hackathonService;

    public HackathonController(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hackathon createHackathon(@RequestBody CreateHackathonRequest request) {
        HackathonBuilder builder = new HackathonBuilder().setName(request.name())
                .setRegulation(request.regulation()).setLocation(request.location())
                .setRegistrationDeadline(request.registrationDeadline()).setStartDate(request.startDate())
                .setEndDate(request.endDate()).setPrize(request.prize())
                .setMaxTeamMembers(request.maxTeamMembers());
        return hackathonService.createHackathon(builder);
    }

    @PostMapping("/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse registerTeam(@PathVariable("teamId") Long teamId,
                                             @RequestBody RegisterTeamRequest request) {
        Registration registration = hackathonService.registerTeam(request.hackathonName(), teamId);
        return new RegistrationResponse(registration.getRegistrationId(), request.hackathonName().trim(), teamId);
    }

    @PostMapping("/{hackathonId}/mentors/{mentorId}")
    public Invitation addMentor(@PathVariable("hackathonId") Long hackathonId, @PathVariable("mentorId") Long mentorId) {
        return hackathonService.addMentor(hackathonId, mentorId);
    }

    @PostMapping("/teams/{teamId}/mentor/{mentorId}")
    public Team addMentorToTeam(@PathVariable("teamId") Long teamId, @PathVariable("mentorId") Long mentorId) {
        return hackathonService.addMentorToTeam(teamId, mentorId);
    }

    @PostMapping("/{hackathonId}/judges/{judgeId}")
    public Invitation addJudge(@PathVariable("hackathonId") Long hackathonId, @PathVariable("judgeId") Long judgeId) {
        return hackathonService.addJudge(hackathonId, judgeId);
    }

    @PutMapping("/{hackathonId}/state")
    public Hackathon changeState(@PathVariable("hackathonId") Long hackathonId, @RequestBody StateRequest request) {
        return hackathonService.changeState(hackathonId, request.state());
    }

    @PostMapping("/{hackathonId}/winner/{teamId}")
    public Hackathon declareWinner(@PathVariable("hackathonId") Long hackathonId, @PathVariable("teamId") Long teamId) {
        return hackathonService.declareWinner(hackathonId, teamId);
    }

    @PostMapping("/{hackathonId}/leaderboard/publish")
    public Hackathon publishLeaderboard(@PathVariable("hackathonId") Long hackathonId) {
        return hackathonService.publishLeaderboard(hackathonId);
    }

    @GetMapping("/{hackathonId}/leaderboard")
    public List<String> getLeaderboard(@PathVariable("hackathonId") Long hackathonId) {
        return hackathonService.getLeaderboard(hackathonId);
    }

    @PostMapping("/{hackathonId}/prize")
    public Payment awardPrize(@PathVariable("hackathonId") Long hackathonId) {
        return hackathonService.awardPrize(hackathonId);
    }

    @GetMapping
    public List<Hackathon> viewHackathons() {
        return hackathonService.viewHackathons();
    }

    @GetMapping("/{hackathonId}")
    public Hackathon getHackathonDetails(@PathVariable("hackathonId") Long hackathonId) {
        return hackathonService.getHackathonDetails(hackathonId);
    }

    @GetMapping("/{hackathonId}/teams")
    public List<Team> viewRegisteredTeams(@PathVariable("hackathonId") Long hackathonId) {
        return hackathonService.viewRegisteredTeams(hackathonId);
    }

    @GetMapping("/teams/{teamId}/available-mentors")
    public List<Mentor> viewAvailableMentors(@PathVariable("teamId") Long teamId) {
        return hackathonService.viewAvailableMentors(teamId);
    }

    public record CreateHackathonRequest(String name, String regulation, String location,
                                         LocalDate registrationDeadline, LocalDate startDate,
                                         LocalDate endDate, double prize, int maxTeamMembers) {}
    public record StateRequest(HackathonState state) {}
    public record RegisterTeamRequest(String hackathonName) {}
    public record RegistrationResponse(Long registrationId, String hackathonName, Long teamId) {}
}
