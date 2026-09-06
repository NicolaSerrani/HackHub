package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Payment;

public interface PaymentRepository {
    void save(Payment payment);
    Payment findById(Long id);
    Payment findByHackathon(Long hackathonId);
}
