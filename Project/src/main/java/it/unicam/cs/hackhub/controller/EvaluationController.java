package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Evaluation;
import it.unicam.cs.hackhub.service.EvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/{submissionId}/evaluation")
    @ResponseStatus(HttpStatus.CREATED)
    public Evaluation evaluateSubmission(@PathVariable Long submissionId,
                                         @RequestBody EvaluationRequest request) {
        return evaluationService.evaluateSubmission(submissionId, request.judgeId(),
                request.score(), request.comment());
    }

    public record EvaluationRequest(Long judgeId, double score, String comment) {}
}
