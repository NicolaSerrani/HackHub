package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findBySubmission_SubmissionId(Long submissionId);
}
