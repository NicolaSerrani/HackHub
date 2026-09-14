package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Team createTeam(@RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request.name());
    }

    @GetMapping("/{teamId}")
    public Team getTeam(@PathVariable("teamId") Long teamId) {
        return teamService.findTeam(teamId);
    }

    @PostMapping("/{teamId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Invitation> inviteUsers(@PathVariable("teamId") Long teamId, @RequestBody InviteUsersRequest request) {
        return teamService.inviteUsers(teamId, request.userIds());
    }

    @PostMapping("/{teamId}/violations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reportViolation(@PathVariable("teamId") Long teamId, @RequestBody ViolationRequest request) {
        teamService.reportViolation(teamId, request.mentorId(), request.description());
    }

    public record CreateTeamRequest(String name) {}
    public record InviteUsersRequest(List<Long> userIds) {}
    public record ViolationRequest(Long mentorId, String description) {}
}
