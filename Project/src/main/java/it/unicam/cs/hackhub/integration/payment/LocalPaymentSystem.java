package it.unicam.cs.hackhub.integration.payment;

import it.unicam.cs.hackhub.model.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class LocalPaymentSystem implements PaymentSystem {
    @Override
    public boolean requestPayment(Payment payment) {
        return payment != null && payment.validate();
    }
}
