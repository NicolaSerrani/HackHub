package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Evaluation;
import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Submission;
import it.unicam.cs.hackhub.repository.EvaluationRepository;
import it.unicam.cs.hackhub.repository.SubmissionRepository;
import it.unicam.cs.hackhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final SubmissionRepository submissionRepository;
    private final UserService userService;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             SubmissionRepository submissionRepository,
                             UserService userService) {
        this.evaluationRepository = evaluationRepository;
        this.submissionRepository = submissionRepository;
        this.userService = userService;
    }

    public Evaluation evaluateSubmission(Long submissionId, Long judgeId, double score, String comment) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));
        Judge judge = userService.requireRole(Judge.class);
        if (!judge.getUserId().equals(judgeId)) {
            throw new IllegalStateException("A judge can evaluate only as themselves.");
        }
        if (submission.getHackathon().getJudge() == null
                || !judge.getUserId().equals(submission.getHackathon().getJudge().getUserId())) {
            throw new IllegalStateException("Judge is not assigned to this hackathon.");
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Evaluation comment cannot be blank");
        }
        if (evaluationRepository.findBySubmission_SubmissionId(submissionId).isPresent()) {
            throw new IllegalStateException("Submission has already been evaluated");
        }
        Evaluation evaluation = judge.evaluateSubmission(submission, score, comment);
        submissionRepository.save(submission);
        return evaluationRepository.save(evaluation);
    }
}
