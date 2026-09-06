package it.unicam.cs.hackhub.model.entity;

import it.unicam.cs.hackhub.model.enumeration.PaymentState;

import java.time.LocalDateTime;

public class Payment {

    private Long paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentState state;
    private Hackathon hackathon;
    private Team recipient;

    public Payment() {
        this.state = PaymentState.PENDING;
    }

    public void execute() {
        if (!validate()) {
            throw new IllegalStateException("Payment data is incomplete.");
        }
        if (state != PaymentState.PENDING) {
            throw new IllegalStateException("Only pending payments can be executed.");
        }
    }

    public void confirm() {
        if (state != PaymentState.PENDING) {
            throw new IllegalStateException("Only pending payments can be confirmed.");
        }
        state = PaymentState.COMPLETED;
        paymentDate = LocalDateTime.now();
    }

    public void reject() {
        if (state != PaymentState.PENDING) {
            throw new IllegalStateException("Only pending payments can be rejected.");
        }
        state = PaymentState.REJECTED;
        paymentDate = LocalDateTime.now();
    }

    public boolean validate() {
        return amount >= 0 && hackathon != null && recipient != null;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public PaymentState getState() {
        return state;
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

    public Team getRecipient() {
        return recipient;
    }

    public void setRecipient(Team recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("Payment recipient cannot be null.");
        }
        this.recipient = recipient;
    }
}
