package com.example.Taller_Tienda.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Taller_Tienda.Model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
