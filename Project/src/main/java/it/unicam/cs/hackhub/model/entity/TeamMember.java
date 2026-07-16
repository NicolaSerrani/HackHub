package it.unicam.cs.hackhub.model.entity;

public class TeamMember extends User {

    private Team team;

    public TeamMember() {
        super();
    }

    public Team createTeam(String teamName) {

        Team team = new Team();
        team.setName(teamName);
        team.addMember(this);

        this.team = team;

        return team;
    }

    public void inviteUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
    }

    public void registerTeam(Hackathon hackathon) {
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        Registration registration = new Registration();
        hackathon.registerTeam(registration);
    }

    public void submitSubmission(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        submission.submit();
    }

    public boolean belongsToTeam() {
        return team != null;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}