package it.unicam.cs.hackhub.integration.payment;

import it.unicam.cs.hackhub.model.entity.Payment;

public interface PaymentSystem {
    boolean requestPayment(Payment payment);
}
