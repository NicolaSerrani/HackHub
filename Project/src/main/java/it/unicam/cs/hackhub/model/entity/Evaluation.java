package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.pattern.strategy.EvaluationStrategy;

import java.time.LocalDateTime;

public class Evaluation {

    private Long evaluationId;
    private double score;
    private String comment;
    private LocalDateTime evaluationDate;

    private Judge judge;
    private Submission submission;

    private EvaluationStrategy strategy;

    public Evaluation() {
        this.evaluationDate = LocalDateTime.now();
    }

    public boolean validate() {
        return score >= 0 && score <= 10;
    }

    public double calculateScore() {

        if (strategy == null) {
            throw new IllegalStateException("Evaluation strategy not set.");
        }

        score = strategy.evaluate(submission);
        return score;
    }

    public void assignStrategy(EvaluationStrategy strategy) {
        this.strategy = strategy;
    }

    public Long getEvaluationId() {
        return evaluationId;
    }

    public double getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public Judge getJudge() {
        return judge;
    }

    public Submission getSubmission() {
        return submission;
    }

    public EvaluationStrategy getStrategy() {
        return strategy;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public void setScore(double score) {
        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("Score must be between 0 and 10.");
        }
        this.score = score;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

}
