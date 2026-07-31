package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.User;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeamService {

    private static final Map<Long, Team> teams = new LinkedHashMap<>();
    private static long nextTeamId = 1;
    private String teamName;
    private Team selectedTeam;
    private List<User> selectedUsers;

    public void createTeam(String name) {
        teamName = name;
        if (!validateTeamName()) {
            throw new IllegalArgumentException("Team name cannot be blank");
        }
        Team team = new Team();
        team.setTeamId(nextTeamId++);
        team.setName(name);
        teams.put(team.getTeamId(), team);
        selectedTeam = team;
    }

    public void inviteUsers(Long teamId, List<User> users) {
        selectedTeam = findTeam(teamId);
        selectedUsers = users;
        if (!checkTeam() || !checkSelectedUsers() || !checkTeamSize() || !checkAvailability()) {
            throw new IllegalStateException("Users cannot be invited to this team");
        }
        for (User user : selectedUsers) {
            Invitation invitation = new Invitation();
            invitation.setInvitationId(InvitationService.nextInvitationId());
            invitation.setType(InvitationType.TEAM);
            selectedTeam.addInvitation(invitation);
            user.addInvitation(invitation);
            InvitationService.storeInvitation(invitation);
        }
    }

    public List<Team> viewRegisteredTeams(Long hackathonId) {
        return HackathonService.findHackathon(hackathonId).getRegistrations().stream()
                .filter(registration -> registration.isActive())
                .map(registration -> registration.getTeam())
                .toList();
    }

    public void reportViolation(Long teamId, String description) {
        selectedTeam = findTeam(teamId);
        if (!checkTeam() || !checkDescription(description)) {
            throw new IllegalArgumentException("Invalid violation report");
        }
        selectedTeam.reportViolation(new Mentor(), description);
    }

    static Team getCurrentTeam() {
        return teams.isEmpty() ? null : teams.values().stream().reduce((first, second) -> second).orElse(null);
    }

    private boolean validateTeamName() {
        return teamName != null && !teamName.isBlank();
    }

    private boolean checkUserAlreadyInTeam() {
        return selectedUsers.stream().anyMatch(user -> selectedTeam.getInvitations().stream()
                .anyMatch(invitation -> invitation.getReceiver() == user && invitation.isPending()));
    }

    private boolean checkSelectedUsers() {
        return selectedUsers != null && !selectedUsers.isEmpty()
                && selectedUsers.stream().allMatch(user -> user != null)
                && !checkUserAlreadyInTeam();
    }

    private boolean checkTeamSize() {
        return selectedTeam.getMemberCount() + selectedUsers.size() > selectedTeam.getMemberCount();
    }

    private boolean checkRegisteredTeams() {
        return !teams.isEmpty();
    }

    private boolean checkDescription(String description) {
        return description != null && !description.isBlank();
    }

    private boolean checkTeam() {
        return selectedTeam != null;
    }

    private boolean checkAvailability() {
        return checkRegisteredTeams();
    }

    private Team findTeam(Long teamId) {
        Team team = teams.get(teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team not found: " + teamId);
        }
        return team;
    }
}
