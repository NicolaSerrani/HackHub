package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;

public class Organizer extends User {

    public Organizer() {
        super();
    }

    public Hackathon createHackathon(HackathonBuilder builder) {

        if (builder == null) {
            throw new IllegalArgumentException("HackathonBuilder cannot be null.");
        }

        return builder.build();
    }

    public void addMentor(Hackathon hackathon, Mentor mentor) {

        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        if (mentor == null) {
            throw new IllegalArgumentException("Mentor cannot be null.");
        }

        hackathon.addMentor(mentor);
    }

    public void assignJudge(Hackathon hackathon, Judge judge) {

        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        if (judge == null) {
            throw new IllegalArgumentException("Judge cannot be null.");
        }

        hackathon.setJudge(judge);
    }

    public void declareWinner(Hackathon hackathon, Team winner) {

        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        if (winner == null) {
            throw new IllegalArgumentException("Winner cannot be null.");
        }

        hackathon.setWinner(winner);
    }

    public void publishLeaderboard(Hackathon hackathon) {

        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        hackathon.publishLeaderboard();
    }

}
