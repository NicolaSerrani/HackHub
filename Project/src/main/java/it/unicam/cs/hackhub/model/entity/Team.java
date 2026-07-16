package it.unicam.cs.hackhub.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

    private Long teamId;
    private String name;

    private final List<TeamMember> members;
    private final List<SupportRequest> supportRequests;

    public Team() {
        this.members = new ArrayList<>();
        this.supportRequests = new ArrayList<>();
    }

    public void addMember(TeamMember member) {
        if (member != null && !members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(TeamMember member) {
        members.remove(member);
    }

    public boolean containsMember(TeamMember member) {
        return members.contains(member);
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public void addSupportRequest(SupportRequest request) {
        if (request != null) {
            supportRequests.add(request);
        }
    }

    public void removeSupportRequest(SupportRequest request) {
        supportRequests.remove(request);
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public List<TeamMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<SupportRequest> getSupportRequests() {
        return Collections.unmodifiableList(supportRequests);
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public void setName(String name) {
        this.name = name;
    }

}
