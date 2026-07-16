package it.unicam.cs.hackhub.pattern.builder;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Mentor;

import java.time.LocalDate;

public class HackathonBuilder {

    private final Hackathon hackathon;

    public HackathonBuilder() {
        this.hackathon = new Hackathon();
    }

    public HackathonBuilder setName(String name) {
        hackathon.setName(name);
        return this;
    }

    public HackathonBuilder setRegulation(String regulation) {
        hackathon.setRegulation(regulation);
        return this;
    }

    public HackathonBuilder setLocation(String location) {
        hackathon.setLocation(location);
        return this;
    }

    public HackathonBuilder setRegistrationDeadline(LocalDate registrationDeadline) {
        hackathon.setRegistrationDeadline(registrationDeadline);
        return this;
    }

    public HackathonBuilder setStartDate(LocalDate startDate) {
        hackathon.setStartDate(startDate);
        return this;
    }

    public HackathonBuilder setEndDate(LocalDate endDate) {
        hackathon.setEndDate(endDate);
        return this;
    }

    public HackathonBuilder setPrize(double prize) {
        hackathon.setPrize(prize);
        return this;
    }

    public HackathonBuilder setMaxTeamMembers(int maxTeamMembers) {
        hackathon.setMaxTeamMembers(maxTeamMembers);
        return this;
    }

    public HackathonBuilder assignJudge(Judge judge) {
        hackathon.setJudge(judge);
        return this;
    }

    public HackathonBuilder addMentor(Mentor mentor) {
        hackathon.addMentor(mentor);
        return this;
    }

    public Hackathon build() {
        return hackathon;
    }

}
