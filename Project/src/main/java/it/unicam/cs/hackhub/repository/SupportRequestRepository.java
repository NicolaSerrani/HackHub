package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByMentor_UserId(Long mentorId);
    List<SupportRequest> findByHackathon_HackathonId(Long hackathonId);
}
