/* package com.example.Taller_Tienda.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.Service.KafkaPrueba;


@RestController
@RequestMapping("/proveedorA")
public class ProveedorAController {

    @Autowired
    private KafkaPrueba notificaciones;

    
    @PostMapping("/notificar")
    public String notificar(@RequestBody String mensaje) {
        notificaciones.enviarNotificacion(mensaje);
        return "Notificación enviada al proveedor A";
    }

}
 */