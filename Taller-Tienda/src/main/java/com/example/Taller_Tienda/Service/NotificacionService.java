package com.example.Taller_Tienda.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.Taller_Tienda.Model.Notificacion;

@Service
public class NotificacionService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    
    public void enviarEmail(Notificacion notificacion) {
        log.info("Enviando email a usuario: {}", notificacion.getUsuarioId());
        // Implementar envío de email
    }
    
}
