package com.example.Taller_Tienda.Service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.Taller_Tienda.Model.Payment;
import com.example.Taller_Tienda.Repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Long orderId, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        return paymentRepository.save(payment);
    }
}
