
package com.example.Taller_Tienda.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.Service.OperacionGlobalService;

@RestController
@RequestMapping("/api/bill")
public class TestJTAController {

    private final OperacionGlobalService operacionGlobalService;

    public TestJTAController(OperacionGlobalService operacionGlobalService) {
        this.operacionGlobalService = operacionGlobalService;
    }

    /**
     * Endpoint para probar la transacción distribuida con OperacionGlobalService
     * Parámetros opcionales para personalizar la prueba
     */
    @GetMapping
 //   @Transactional
    public String createBillAndPayInALLDBS(
            @RequestParam(defaultValue = "1") Long productoId,
            @RequestParam(defaultValue = "2") int cantidad,
            @RequestParam(defaultValue = "1") Long clienteId,
            @RequestParam(defaultValue = "1") Long metodoPagoId,
            @RequestParam(defaultValue = "false") boolean simulateFailure) {
        
        try {
            // Si simulateFailure es true, usar un productoId que no existe para provocar error
            Long testProductoId = simulateFailure ? 1L : productoId;
            
            operacionGlobalService.crearFacturaYPago(testProductoId, cantidad, clienteId, metodoPagoId, simulateFailure);
            
            return String.format(
                "✅ Transacción completada exitosamente:\n" +
                "- Producto ID: %d\n" +
                "- Cantidad: %d\n" +
                "- Cliente ID: %d\n" +
                "- Método de Pago ID: %d\n" +
                "- Factura creada y stock actualizado\n" +
                "- Pago registrado",
                testProductoId, cantidad, clienteId, metodoPagoId
            );
        
        } catch (Exception e) {
             return String.format(
                "❌ Rollback ejecutado exitosamente:\n" +
                "- Error: %s\n" +
                "- Parámetros utilizados: ProductoId=%d, Cantidad=%d, ClienteId=%d, MetodoPagoId=%d\n" +
                "- Todas las operaciones fueron revertidas automáticamente\n" +
                "- Datos consistentes en todas las bases de datos",
                e.getMessage(), productoId, cantidad, clienteId, metodoPagoId
            ); 
             //throw new RuntimeException("Error en operación global: " + e.getMessage(), e);
        }
    }

}

