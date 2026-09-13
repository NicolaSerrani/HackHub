package it.unicam.cs.hackhub.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("JUDGE")
public class Judge extends StaffMember {
    public Evaluation evaluateSubmission(Submission submission, double score, String comment) {
        if (submission == null) throw new IllegalArgumentException("Submission cannot be null.");
        Evaluation evaluation = new Evaluation();
        evaluation.setJudge(this);
        evaluation.setSubmission(submission);
        evaluation.setScore(score);
        evaluation.setComment(comment);
        submission.addEvaluation(evaluation);
        return evaluation;
    }
}
