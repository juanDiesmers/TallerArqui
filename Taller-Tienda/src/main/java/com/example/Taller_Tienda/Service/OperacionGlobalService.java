package com.example.Taller_Tienda.Service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.Taller_Tienda.ModelJTA.facturacion.Factura;
import com.example.Taller_Tienda.ModelJTA.facturacion.FacturaItem;
import com.example.Taller_Tienda.ModelJTA.pago.Pago;
import com.example.Taller_Tienda.RepositoryJTA.factura.FacturaItemRepository;
import com.example.Taller_Tienda.RepositoryJTA.factura.FacturaRepository;
import com.example.Taller_Tienda.RepositoryJTA.inventario.ProductoRepository;
import com.example.Taller_Tienda.RepositoryJTA.pago.PagoRepository;

@Service
public class OperacionGlobalService {

    private final FacturaRepository facturaRepo;
    private final FacturaItemRepository itemRepo;
    private final PagoRepository pagoRepo;
    private final ProductoRepository productoRepo;

    public OperacionGlobalService(
            FacturaRepository facturaRepo,
            FacturaItemRepository itemRepo,
            PagoRepository pagoRepo,
            ProductoRepository productoRepo) {
        this.facturaRepo = facturaRepo;
        this.itemRepo = itemRepo;
        this.pagoRepo = pagoRepo;
        this.productoRepo = productoRepo;
    }

    /**
     * Crea una factura, descuenta stock y registra un pago.
     * Todo dentro de una transacción JTA distribuida.
     */
    @Transactional(
    timeout = 120,
    isolation = Isolation.READ_COMMITTED
    )
    public void crearFacturaYPago(Long productoId, int cantidad, Long clienteId, Long metodoPagoId, boolean simulateFailure) {
        
            // Desactivar FK checks temporalmente
            facturaRepo.flush();
            
            // 1️⃣ Obtener producto
            var producto = productoRepo.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < cantidad)
                throw new RuntimeException("Stock insuficiente");

            // 2️⃣ Crear factura
            var factura = new Factura();
            factura.setNumero("F-" + System.currentTimeMillis());
            factura.setClienteId(clienteId);
            factura.setTotalCentavos(producto.getPrecio()
                    .multiply(BigDecimal.valueOf(cantidad))
                    .longValue());
            factura.setEstado(Factura.EstadoFactura.EMITIDA);
            facturaRepo.save(factura);
            facturaRepo.flush();
            
            System.out.println("✅ Factura creada con ID: " + factura.getId());

            // 3️⃣ Crear ítem
            var item = new FacturaItem();
            item.setFactura(factura);
            item.setProductoId(productoId);
            item.setCantidad(cantidad);
            item.setPrecioUnitarioCentavos(producto.getPrecio()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue());
            itemRepo.save(item);
            itemRepo.flush();
            
            System.out.println("✅ Item creado");

            if (simulateFailure)
                throw new RuntimeException("Falla simulada después de crear factura e ítem");

            // 4️⃣ Actualizar stock
            producto.setStock(producto.getStock() - cantidad);
            productoRepo.save(producto);
            productoRepo.flush();
            
            System.out.println("✅ Stock actualizado");

            // 5️⃣ Crear pago - AQUÍ SE BLOQUEA
            System.out.println("⏳ Intentando crear pago para factura ID: " + factura.getId());
            
            var pago = new Pago();
            pago.setFacturaId(factura.getId());
            pago.setMontoCentavos(factura.getTotalCentavos());
            pago.setMoneda("COP");
            pago.setEstado(Pago.EstadoPago.APLICADO); 
            
            System.out.println("⏳ Guardando pago...");
            pagoRepo.save(pago);
            pagoRepo.flush();
            
            System.out.println("✅ Pago creado exitosamente");
            
    }
}
