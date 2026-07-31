package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.service.EvaluationService;

public class EvaluationController {

    private final EvaluationService evaluationService = new EvaluationService();

    public void evaluateSubmission(Long submissionId, double score, String comment) {
        evaluationService.valutaSottomissione(submissionId, score, comment);
    }
}
