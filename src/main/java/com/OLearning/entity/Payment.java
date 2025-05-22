package com.OLearning.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentId")
    private int paymentId;
    @Column(name = "Amount")
    private double amount;
    @Column(name = "Status")
    private String status;
    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;
    @Column(name = "Note")
    private String note;
    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    public Payment() {
    }

    public Payment(double amount, String status, LocalDateTime paymentDate, String note, User user) {
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.note = note;
        this.user = user;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
