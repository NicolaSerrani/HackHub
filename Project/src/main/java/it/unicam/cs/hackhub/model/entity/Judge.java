package it.unicam.cs.hackhub.model.entity;

public class Judge extends StaffMember {

    public Judge() {
        super();
    }

    public Evaluation evaluateSubmission(
            Submission submission,
            double score,
            String comment
    ) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Score must be between 0 and 100."
            );
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setJudge(this);
        evaluation.setSubmission(submission);
        evaluation.setScore(score);
        evaluation.setComment(comment);

        submission.addEvaluation(evaluation);

        return evaluation;
    }
}