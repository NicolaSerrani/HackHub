package it.unicam.cs.hackhub.pattern.strategy;

import it.unicam.cs.hackhub.model.entity.Submission;

public class StandardEvaluationStrategy implements EvaluationStrategy {

    @Override
    public double evaluate(Submission submission) {

        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        return 100.0;
    }

}