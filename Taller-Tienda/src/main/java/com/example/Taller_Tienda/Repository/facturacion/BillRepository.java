package com.example.Taller_Tienda.Repository.facturacion;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Taller_Tienda.Model.Bill;

public interface BillRepository extends JpaRepository<Bill,Long> {
    
}
