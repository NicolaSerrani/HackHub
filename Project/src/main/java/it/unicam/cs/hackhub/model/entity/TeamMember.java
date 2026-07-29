package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.InvitationType;

import java.util.List;

public class TeamMember {

    private Team team;

    public TeamMember() {
    }

    public Team createTeam(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            throw new IllegalArgumentException(
                    "Team name cannot be null or blank."
            );
        }

        Team newTeam = new Team();
        newTeam.setName(teamName);
        newTeam.addMember(this);

        this.team = newTeam;

        return newTeam;
    }

    public void inviteUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException(
                    "The users list cannot be null or empty."
            );
        }

        getTeam();

        for (User user : users) {
            if (user == null) {
                continue;
            }

            Invitation invitation = new Invitation();
            invitation.setType(InvitationType.TEAM);
            invitation.setReceiver(user);

            user.addInvitation(invitation);
        }
    }

    public void registerTeam(Hackathon hackathon) {
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        hackathon.registerTeam(getTeam());
    }

    public void submitSubmission(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        submission.setTeam(getTeam());
        submission.submit();
    }

    public Team getTeam() {
        if (team == null) {
            throw new IllegalStateException(
                    "The team member does not belong to a team."
            );
        }

        return team;
    }

    public void setTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        this.team = team;
    }
}