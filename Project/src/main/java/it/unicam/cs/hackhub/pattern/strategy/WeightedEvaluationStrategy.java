package it.unicam.cs.hackhub.pattern.strategy;

import it.unicam.cs.hackhub.model.entity.Submission;

public class WeightedEvaluationStrategy implements EvaluationStrategy {

    private final double originalityWeight;
    private final double technicalWeight;
    private final double presentationWeight;

    public WeightedEvaluationStrategy() {
        this(0.4, 0.4, 0.2);
    }

    public WeightedEvaluationStrategy(double originalityWeight,
                                      double technicalWeight,
                                      double presentationWeight) {

        this.originalityWeight = originalityWeight;
        this.technicalWeight = technicalWeight;
        this.presentationWeight = presentationWeight;
    }

    @Override
    public double evaluate(Submission submission) {

        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null.");
        }

        double originalityScore = 90.0;
        double technicalScore = 85.0;
        double presentationScore = 95.0;

        return originalityScore * originalityWeight
                + technicalScore * technicalWeight
                + presentationScore * presentationWeight;
    }

}