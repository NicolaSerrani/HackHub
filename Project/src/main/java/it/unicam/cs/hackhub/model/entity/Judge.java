package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.pattern.strategy.EvaluationStrategy;

public class Judge extends User {

    public Judge() {
        super();
    }

    public Evaluation evaluateSubmission(Submission submission,
                                         double score,
                                         String comment) {

        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        Evaluation evaluation = new Evaluation();

        evaluation.setJudge(this);
        evaluation.setSubmission(submission);
        evaluation.setScore(score);
        evaluation.setComment(comment);

        submission.addEvaluation(evaluation);

        return evaluation;
    }

    public Evaluation evaluateSubmission(Submission submission,
                                         String comment,
                                         EvaluationStrategy strategy) {

        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Evaluation strategy cannot be null.");
        }

        Evaluation evaluation = new Evaluation();

        evaluation.setJudge(this);
        evaluation.setSubmission(submission);
        evaluation.setComment(comment);
        evaluation.assignStrategy(strategy);
        evaluation.calculateScore();

        submission.addEvaluation(evaluation);

        return evaluation;
    }

}
