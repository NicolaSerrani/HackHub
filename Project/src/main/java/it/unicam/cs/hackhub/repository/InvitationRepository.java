package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Invitation;
import it.unicam.cs.hackhub.model.enumeration.InvitationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByReceiver_UserId(Long userId);
    Optional<Invitation> findByInvitationIdAndType(Long invitationId, InvitationType type);
}
