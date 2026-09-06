package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallRepository extends JpaRepository<Call, Long> {
    List<Call> findByTeam_TeamId(Long teamId);
}
