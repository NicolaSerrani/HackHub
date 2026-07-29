package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;

public class Organizer extends StaffMember {

    private Hackathon currentHackathon;

    public Organizer() {
        super();
    }

    public Hackathon createHackathon(HackathonBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("HackathonBuilder cannot be null.");
        }

        Hackathon hackathon = builder.build();

        if (hackathon == null) {
            throw new IllegalStateException("The builder did not create an hackathon.");
        }

        addHackathon(hackathon);
        currentHackathon = hackathon;

        return hackathon;
    }

    public void addMentor(Mentor mentor) {
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor cannot be null.");
        }

        getCurrentHackathon().addMentor(mentor);
    }

    public void declareWinner(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        getCurrentHackathon().setWinner(team);
    }

    public Hackathon getCurrentHackathon() {
        if (currentHackathon == null) {
            throw new IllegalStateException("No hackathon has been selected.");
        }

        return currentHackathon;
    }

    public void setCurrentHackathon(Hackathon currentHackathon) {
        if (currentHackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        if (!viewHackathons().contains(currentHackathon)) {
            addHackathon(currentHackathon);
        }

        this.currentHackathon = currentHackathon;
    }
}