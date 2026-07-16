package it.unicam.cs.hackhub.pattern.strategy;

import it.unicam.cs.hackhub.model.entity.Submission;

public interface EvaluationStrategy {

    double evaluate(Submission submission);

}