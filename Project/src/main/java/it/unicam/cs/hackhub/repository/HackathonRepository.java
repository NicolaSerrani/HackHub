package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
}
