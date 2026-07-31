package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.pattern.builder.HackathonBuilder;
import it.unicam.cs.hackhub.service.HackathonService;

import java.util.List;

public class HackathonController {

    private final HackathonService hackathonService = new HackathonService();

    public void createHackathon(HackathonBuilder builder) {
        hackathonService.createHackathon(builder);
    }

    public void registerTeam(Long hackathonId) {
        hackathonService.registerTeam(hackathonId);
    }

    public void addMentor(Long hackathonId, Long mentorId) {
        hackathonService.addMentor(hackathonId, mentorId);
    }

    public void addJudge(Long hackathonId, Long judgeId) {
        hackathonService.addJudge(hackathonId, judgeId);
    }

    public void declareWinner(Long hackathonId, Long teamId) {
        hackathonService.declareWinner(hackathonId, teamId);
    }

    public void publishLeaderboard(Long hackathonId) {
        hackathonService.publishLeaderboard(hackathonId);
    }

    public List<Team> getLeaderboard(Long hackathonId) {
        return hackathonService.getLeaderboard(hackathonId);
    }

    public List<Hackathon> viewHackathons() {
        return hackathonService.viewHackathons();
    }

    public Hackathon getHackathonDetails(Long hackathonId) {
        return hackathonService.getHackathonDetails(hackathonId);
    }
}
