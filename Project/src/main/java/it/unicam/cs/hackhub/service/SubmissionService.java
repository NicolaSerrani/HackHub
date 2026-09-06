package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.entity.Hackathon;
import it.unicam.cs.hackhub.model.entity.Submission;
import it.unicam.cs.hackhub.model.entity.Team;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.SubmissionRepository;
import it.unicam.cs.hackhub.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;

    public SubmissionService(SubmissionRepository submissionRepository, TeamRepository teamRepository,
                             HackathonRepository hackathonRepository) {
        this.submissionRepository = submissionRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
    }

    public Submission saveDraft(Long teamId, Long hackathonId, String title,
                                String description, String repositoryUrl) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found: " + hackathonId));
        Submission submission = new Submission();
        submission.setTeam(team);
        submission.setHackathon(hackathon);
        submission.update(title, description, repositoryUrl);
        submission.saveDraft();
        return submissionRepository.save(submission);
    }

    public Submission saveDraft(Submission submission) {
        if (submission == null || submission.getTeam() == null || submission.getHackathon() == null) {
            throw new IllegalArgumentException("Submission, team and hackathon are required");
        }
        submission.saveDraft();
        return submissionRepository.save(submission);
    }

    public Submission submitSubmission(Long submissionId) {
        Submission submission = getSubmission(submissionId);
        submission.submit();
        return submissionRepository.save(submission);
    }

    public Submission updateSubmission(Long submissionId, String title, String description, String repositoryUrl) {
        Submission submission = getSubmission(submissionId);
        submission.update(title, description, repositoryUrl);
        return submissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public List<Submission> viewSubmissions(Long hackathonId) {
        return submissionRepository.findByHackathon_HackathonId(hackathonId);
    }

    @Transactional(readOnly = true)
    public Submission getSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));
    }
}
