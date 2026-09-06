package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByHackathon_HackathonId(Long hackathonId);
    boolean existsByHackathon_HackathonIdAndTeam_TeamId(Long hackathonId, Long teamId);

    @Query("select r.team from Registration r where r.hackathon.hackathonId = :hackathonId and r.state = it.unicam.cs.hackhub.model.enumeration.RegistrationState.ACTIVE")
    List<it.unicam.cs.hackhub.model.entity.Team> findActiveTeams(@Param("hackathonId") Long hackathonId);
}
