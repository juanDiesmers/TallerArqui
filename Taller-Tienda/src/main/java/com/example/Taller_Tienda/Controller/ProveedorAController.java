package com.example.Taller_Tienda.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.ModelProveedorA.Factura;
import com.example.Taller_Tienda.Service.ProveedorAService;

@RestController
@RequestMapping("/proveedorA")
public class ProveedorAController {

    @Autowired
    private ProveedorAService proveedorAService;

    @PostMapping("/notificar")
    public String notificar(@RequestBody String mensaje) {
        proveedorAService.enviarNotificacion(mensaje);
        return "Notificación enviada al proveedor A";
    }

    @PostMapping("/factura")
    public String enviarFactura(@RequestBody Factura factura) {
        proveedorAService.enviarFactura(factura);
        return "Factura enviada al proveedor A por Kafka";
    }
}
