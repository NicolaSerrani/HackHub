package it.unicam.cs.hackhub.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEAM_MEMBER")
public class TeamMember extends User {
}
