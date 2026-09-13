package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ORGANIZER")
public class Organizer extends StaffMember {
    public Hackathon createHackathon(HackathonBuilder builder) {
        if (builder == null) throw new IllegalArgumentException("HackathonBuilder cannot be null.");
        return builder.build();
    }

    public void addMentor(Hackathon hackathon, Mentor mentor) {
        hackathon.addMentor(mentor);
    }

    public void declareWinner(Hackathon hackathon, Team team) {
        hackathon.setWinner(team);
    }
}
