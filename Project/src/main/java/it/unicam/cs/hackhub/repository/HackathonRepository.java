package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    Optional<Hackathon> findByNameIgnoreCase(String name);
}
