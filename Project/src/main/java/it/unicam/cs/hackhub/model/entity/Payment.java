package it.unicam.cs.hackhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import it.unicam.cs.hackhub.model.enumeration.PaymentState;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentState state;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false, unique = true)
    private Hackathon hackathon;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_team_id", nullable = false)
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
