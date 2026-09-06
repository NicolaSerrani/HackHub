package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Evaluation;
import it.unicam.cs.hackhub.model.entity.Submission;

import java.util.LinkedHashMap;
import java.util.Map;

public class EvaluationService {

    private final Map<Long, Evaluation> evaluations = new LinkedHashMap<>();
    private long nextEvaluationId = 1;
    private Submission selectedSubmission;
    private double score;
    private String comment;

    public void evaluateSubmission(Long submissionId, double score, String comment) {
        selectedSubmission = SubmissionService.findSubmission(submissionId);
        this.score = score;
        this.comment = comment;
        if (!validateScore() || !validateComment() || checkAlreadyEvaluated() || !checkJudgeAuthorization()) {
            throw new IllegalArgumentException("Invalid evaluation");
        }
        Evaluation evaluation = new Evaluation();
        evaluation.setEvaluationId(nextEvaluationId++);
        evaluation.setSubmission(selectedSubmission);
        evaluation.setScore(score);
        evaluation.setComment(comment);
        selectedSubmission.addEvaluation(evaluation);
        evaluations.put(evaluation.getEvaluationId(), evaluation);
    }

    private boolean validateScore() {
        return score >= 0 && score <= 10;
    }

    private boolean validateComment() {
        return comment != null && !comment.isBlank();
    }

    private boolean checkAlreadyEvaluated() {
        return selectedSubmission.getEvaluation() != null;
    }

    private boolean checkJudgeAuthorization() {
        return selectedSubmission.getState() != null;
    }
}
