package com.example.Taller_Tienda.Service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Taller_Tienda.Model.Bill;
import com.example.Taller_Tienda.Model.Payment;
import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Repository.BillRepository;
import com.example.Taller_Tienda.Repository.PaymentRepository;
import com.example.Taller_Tienda.Repository.ProductRepository;


@Service
public class OrderService {

    private final ProductRepository productRepo;
    private final BillRepository billRepo;
    private final PaymentRepository paymentRepo;

    public OrderService(ProductRepository productRepo,
                        BillRepository billRepo,
                        PaymentRepository paymentRepo) {
        this.productRepo = productRepo;
        this.billRepo = billRepo;
        this.paymentRepo = paymentRepo;
    }

    @Transactional
    public void processOrderRollback() {
        Product p = new Product();
        p.setNombre("Producto de prueba");
        p.setPrecio(new java.math.BigDecimal("99.99"));
        productRepo.save(p);

        Bill b = new Bill();
        b.setId(1L);
        b.setTotal(99999L);
        billRepo.save(b);

        Payment pay = new Payment();
        pay.setAmount(new java.math.BigDecimal("99999"));
        //pay.setStatus("APLICADO");
        paymentRepo.save(pay);

        throw new RuntimeException("💥 Error simulado en la transacción distribuida");
    }
}

