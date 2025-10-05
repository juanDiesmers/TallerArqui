package com.example.Taller_Tienda.Repository.pagos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Taller_Tienda.Model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
