package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByNameIgnoreCase(String name);
}
