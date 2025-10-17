package com.example.Taller_Tienda.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.Model.Notificacion;
import com.example.Taller_Tienda.Model.NotificacionRequest;
import com.example.Taller_Tienda.producer.NotificacionProducer;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    
    @Autowired
    private NotificacionProducer notificacionProducer;
    
    /**
      
      POST /api/notificaciones/enviar
      {
        "usuarioId": "user123",
        "mensaje": "Tu pedido ha sido enviado",
        "tipo": "EMAIL"
      }
     */
    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarNotificacion(
            @RequestBody NotificacionRequest request) {
        
        // Crear notificación
        Notificacion notificacion = new Notificacion(
            UUID.randomUUID().toString(),
            request.getUsuarioId(),
            request.getMensaje(),
            request.getTipo()
        );
        
        // Enviar a Kafka
        notificacionProducer.enviarNotificacion(notificacion);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Notificación enviada a Kafka");
        response.put("notificacionId", notificacion.getId());
        
        return ResponseEntity.ok(response);
    }
    
    /*
     envía una noti EMAIL 
     
     GET /api/notificaciones/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testNotificacion() {
        
        Notificacion notificacion = new Notificacion(
            UUID.randomUUID().toString(),
            "usuario-test-123",
            "Esta es una notificación de prueba desde el controller",
            "EMAIL"
        );
        
        notificacionProducer.enviarNotificacion(notificacion);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Notificación de prueba enviada");
        response.put("notificacionId", notificacion.getId());
        response.put("info", "Revisa los logs para ver el procesamiento");
        
        return ResponseEntity.ok(response);
    }
}
