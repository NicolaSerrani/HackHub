package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Evaluation;
import it.unicam.cs.hackhub.model.entity.Submission;
import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.enumeration.SubmissionState;
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
        Judge judge = userService.requireJudge();
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
        if (submission.getState() != SubmissionState.SUBMITTED) {
            throw new IllegalStateException("Only submitted projects can be evaluated.");
        }
        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("Score must be between 0 and 10.");
        }
        Evaluation evaluation = judge.evaluateSubmission(submission, score, comment);
        return evaluationRepository.save(evaluation);
    }

    public EvaluationResult evaluateSubmissionWithDetails(Long submissionId, Long judgeId,
                                                           double score, String comment) {
        Evaluation evaluation = evaluateSubmission(submissionId, judgeId, score, comment);
        Submission submission = evaluation.getSubmission();
        Team team = submission.getTeam();
        Hackathon hackathon = submission.getHackathon();
        return new EvaluationResult(evaluation.getEvaluationId(), evaluation.getScore(), evaluation.getComment(),
                evaluation.getEvaluationDate(), submission.getSubmissionId(), submission.getTitle(),
                team.getTeamId(), team.getName(), hackathon.getHackathonId(), hackathon.getName());
    }

    public record EvaluationResult(Long evaluationId, double score, String comment,
                                   java.time.LocalDateTime evaluationDate, Long submissionId,
                                   String submissionTitle, Long teamId, String teamName,
                                   Long hackathonId, String hackathonName) {}
}
