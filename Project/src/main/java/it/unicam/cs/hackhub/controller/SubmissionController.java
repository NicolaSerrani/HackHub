package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.model.entity.Submission;
import it.unicam.cs.hackhub.service.SubmissionService;

import java.util.List;

public class SubmissionController {

    private final SubmissionService submissionService = new SubmissionService();

    public void saveDraft(Submission submission) {
        submissionService.saveDraft(submission);
    }

    public void submitSubmission(Long submissionId) {
        submissionService.submitSubmission(submissionId);
    }

    public void updateSubmission(Long submissionId, String title, String description, String repositoryUrl) {
        submissionService.updateSubmission(submissionId, title, description, repositoryUrl);
    }

    public List<Submission> viewSubmissions(Long hackathonId) {
        return submissionService.viewSubmissions(hackathonId);
    }
}
