package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.service.TeamService;

import java.util.List;

public class TeamController {

    private final TeamService teamService = new TeamService();

    public void createTeam(String name) {
        teamService.createTeam(name);
    }

    public void inviteUsers(Long teamId, List<User> users) {
        teamService.inviteUsers(teamId, users);
    }

    public List<Team> viewRegistratedTeams(Long hackathonId) {
        return teamService.viewRegisteredTeams(hackathonId);
    }

    public void reportViolation(Long teamId, String description) {
        teamService.reportViolation(teamId, description);
    }
}
