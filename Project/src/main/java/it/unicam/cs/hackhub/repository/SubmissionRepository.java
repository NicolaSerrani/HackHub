package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByHackathon_HackathonId(Long hackathonId);
    List<Submission> findByTeam_TeamId(Long teamId);
}
