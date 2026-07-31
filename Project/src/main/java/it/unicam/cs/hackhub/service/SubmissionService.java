package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Submission;

import java.util.LinkedHashMap;
import java.util.Map;

public class SubmissionService {

    private static final Map<Long, Submission> submissions = new LinkedHashMap<>();
    private static long nextSubmissionId = 1;

    public void saveDraft(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null");
        }
        if (submission.getSubmissionId() == null) {
            submission.setSubmissionId(nextSubmissionId++);
        }
        submission.saveDraft();
        submissions.put(submission.getSubmissionId(), submission);
    }

    public void submitSubmission(Long submissionId) {
        getSubmission(submissionId).submit();
    }

    public void updateSubmission(Long submissionId, String title, String description, String repositoryUrl) {
        Submission submission = getSubmission(submissionId);
        submission.update(title, description, repositoryUrl);
    }

    public java.util.List<Submission> viewSubmissions(Long hackathonId) {
        return new java.util.ArrayList<>(submissions.values());
    }

    private Submission getSubmission(Long submissionId) {
        return findSubmission(submissionId);
    }

    static Submission findSubmission(Long submissionId) {
        Submission submission = submissions.get(submissionId);
        if (submission == null) {
            throw new IllegalArgumentException("Submission not found: " + submissionId);
        }
        return submission;
    }
}
