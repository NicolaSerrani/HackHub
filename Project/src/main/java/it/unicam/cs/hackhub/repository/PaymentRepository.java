package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByHackathon_HackathonId(Long hackathonId);
}
