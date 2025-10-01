package com.example.Taller_Tienda.Service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.Taller_Tienda.Model.Bill;
import com.example.Taller_Tienda.Repository.BillRepository;

@Service

public class BillService {
    private final BillRepository billRepository ;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill generateInvoice(Long orderId) {
        Bill bill = new Bill();
        bill.setOrderId(orderId);
        bill.setIssuedAt(Instant.now());
        return billRepository.save(bill);
    }

    
}
