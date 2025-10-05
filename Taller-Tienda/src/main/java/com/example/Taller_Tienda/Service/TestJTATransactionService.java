package com.example.Taller_Tienda.Service;



import com.example.Taller_Tienda.Model.Bill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.example.Taller_Tienda.Model.Facturacion;
import com.example.Taller_Tienda.Model.Payment;
import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Repository.facturacion.BillRepository;
import com.example.Taller_Tienda.Repository.pagos.PaymentRepository;
import com.example.Taller_Tienda.Repository.inventario.ProductRepository;


@Service
public class TestJTATransactionService {

    private final ProductRepository productRepo;
    private final BillRepository billRepo;
    private final PaymentRepository paymentRepo;

    public TestJTATransactionService(ProductRepository productRepo,
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
        b.setOrderId(213L);
        billRepo.save(b);

        Payment pay = new Payment();
        pay.setAmount(new java.math.BigDecimal("99999"));
        //pay.setStatus("APLICADO");
        paymentRepo.save(pay);

        throw new RuntimeException("💥 Error simulado en la transacción distribuida");
    }
}

