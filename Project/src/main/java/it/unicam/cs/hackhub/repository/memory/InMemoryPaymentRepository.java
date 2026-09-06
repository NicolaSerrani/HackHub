package it.unicam.cs.hackhub.repository.memory;

import it.unicam.cs.hackhub.model.entity.Payment;
import it.unicam.cs.hackhub.repository.PaymentRepository;

import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<Long, Payment> payments = new LinkedHashMap<>();

    @Override
    public void save(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) {
            throw new IllegalArgumentException("A payment with an ID is required.");
        }
        payments.put(payment.getPaymentId(), payment);
    }

    @Override
    public Payment findById(Long id) {
        return payments.get(id);
    }

    @Override
    public Payment findByHackathon(Long hackathonId) {
        return payments.values().stream()
                .filter(payment -> payment.getHackathon() != null)
                .filter(payment -> hackathonId != null
                        && hackathonId.equals(payment.getHackathon().getHackathonId()))
                .findFirst()
                .orElse(null);
    }
}
