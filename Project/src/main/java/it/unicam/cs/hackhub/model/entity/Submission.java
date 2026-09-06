package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import it.unicam.cs.hackhub.model.enumeration.SubmissionState;

import java.time.LocalDate;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;
    private String title;
    private String description;
    private String repositoryUrl;
    @Enumerated(EnumType.STRING)
    private SubmissionState state;
    private LocalDate submissionDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;
    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL)
    private Evaluation evaluation;

    public Submission() {
        this.state = SubmissionState.DRAFT;
    }

    public void saveDraft() {
        if (state != SubmissionState.DRAFT) {
            throw new IllegalStateException(
                    "Only draft submissions can be saved."
            );
        }
    }

    public void submit() {
        if (state != SubmissionState.DRAFT) {
            throw new IllegalStateException(
                    "Only draft submissions can be submitted."
            );
        }

        if (title == null || title.isBlank()) {
            throw new IllegalStateException(
                    "Submission title cannot be null or blank."
            );
        }

        state = SubmissionState.SUBMITTED;
        submissionDate = LocalDate.now();
    }

    public void setState(SubmissionState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null.");
        }

        this.state = state;
    }

    public void addEvaluation(Evaluation evaluation) {
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation cannot be null.");
        }

        this.evaluation = evaluation;
        this.state = SubmissionState.EVALUATED;
    }

    public void update(
            String title,
            String description,
            String repositoryUrl
    ) {
        if (state != SubmissionState.DRAFT) {
            throw new IllegalStateException(
                    "Only draft submissions can be updated."
            );
        }

        this.title = title;
        this.description = description;
        this.repositoryUrl = repositoryUrl;
    }

    public boolean hasSubmissions() {
        return state == SubmissionState.SUBMITTED
            || state == SubmissionState.EVALUATED;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public SubmissionState getState() {
        return state;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        this.team = team;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setHackathon(Hackathon hackathon) {
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon cannot be null.");
        }
        this.hackathon = hackathon;
    }
}
