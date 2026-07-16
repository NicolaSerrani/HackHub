package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.SubmissionState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Submission {

    private Long submissionId;
    private String title;
    private String description;
    private String repositoryUrl;

    private SubmissionState state;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;

    private Team team;
    private Hackathon hackathon;

    private final List<Evaluation> evaluations;

    public Submission() {
        this.state = SubmissionState.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.evaluations = new ArrayList<>();
    }

    public void saveDraft() {

        if (state != SubmissionState.DRAFT) {
            throw new IllegalStateException("Submission has already been submitted.");
        }

    }

    public void submit() {

        if (state != SubmissionState.DRAFT) {
            throw new IllegalStateException("Submission has already been submitted.");
        }

        state = SubmissionState.SUBMITTED;
        submittedAt = LocalDateTime.now();
    }

    public void update(String title, String description, String repositoryUrl) {

        if (state != SubmissionState.DRAFT) {
            throw new IllegalStateException("Only draft submissions can be updated.");
        }

        this.title = title;
        this.description = description;
        this.repositoryUrl = repositoryUrl;
    }

    public void addEvaluation(Evaluation evaluation) {

        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation cannot be null.");
        }

        evaluations.add(evaluation);

        state = SubmissionState.EVALUATED;
    }

    public boolean isDraft() {
        return state == SubmissionState.DRAFT;
    }

    public boolean isSubmitted() {
        return state == SubmissionState.SUBMITTED;
    }

    public boolean isEvaluated() {
        return state == SubmissionState.EVALUATED;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public SubmissionState getState() {
        return state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public List<Evaluation> getEvaluations() {
        return Collections.unmodifiableList(evaluations);
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

}
