package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Submission;
import it.unicam.cs.hackhub.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Submission saveDraft(@RequestBody SubmissionRequest request) {
        return submissionService.saveDraft(request.teamId(), request.hackathonId(), request.title(),
                request.description(), request.repositoryUrl());
    }

    @PostMapping("/{submissionId}/submit")
    public Submission submitSubmission(@PathVariable Long submissionId) {
        return submissionService.submitSubmission(submissionId);
    }

    @PutMapping("/{submissionId}")
    public Submission updateSubmission(@PathVariable Long submissionId, @RequestBody SubmissionData request) {
        return submissionService.updateSubmission(submissionId, request.title(), request.description(),
                request.repositoryUrl());
    }

    @GetMapping("/hackathon/{hackathonId}")
    public List<Submission> viewSubmissions(@PathVariable Long hackathonId) {
        return submissionService.viewSubmissions(hackathonId);
    }

    public record SubmissionRequest(Long teamId, Long hackathonId, String title,
                                    String description, String repositoryUrl) {}
    public record SubmissionData(String title, String description, String repositoryUrl) {}
}
