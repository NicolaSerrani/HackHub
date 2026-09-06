package it.unicam.cs.hackhub.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StaffMember extends User {

    private final List<Hackathon> hackathons;

    public StaffMember() {
        super();
        this.hackathons = new ArrayList<>();
    }

    public List<Hackathon> viewHackathons() {
        return Collections.unmodifiableList(hackathons);
    }

    public List<Submission> viewSubmissions(Long hackathonId) {
        if (hackathonId == null) {
            throw new IllegalArgumentException("Hackathon ID cannot be null.");
        }

        Hackathon hackathon = hackathons.stream()
                .filter(item -> Objects.equals(item.getHackathonId(), hackathonId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Hackathon not found."));

        return hackathon.getSubmissions().stream()
                .collect(Collectors.toUnmodifiableList());
    }

    public void addHackathon(Hackathon hackathon) {
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }

        if (!hackathons.contains(hackathon)) {
            hackathons.add(hackathon);
        }
    }

    public void removeHackathon(Hackathon hackathon) {
        hackathons.remove(hackathon);
    }
}
