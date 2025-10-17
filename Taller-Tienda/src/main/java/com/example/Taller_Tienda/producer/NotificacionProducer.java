package com.example.Taller_Tienda.producer;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.example.Taller_Tienda.Model.Notificacion;

/**
 * Producer para enviar notificaciones a Kafka
 */
@Component
public class NotificacionProducer {
    
    private static final Logger log = LoggerFactory.getLogger(NotificacionProducer.class);
    
    @Value("${spring.kafka.topic.notificaciones}")
    private String topic;
    
    @Autowired
    private KafkaTemplate<String, Notificacion> kafkaTemplate;
    
   
    public void enviarNotificacion(Notificacion notificacion) {
        log.info("📤 Enviando notificación a Kafka: {}", notificacion.getId());
        
        CompletableFuture<SendResult<String, Notificacion>> future = 
            kafkaTemplate.send(topic, notificacion.getId(), notificacion);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Notificación enviada exitosamente: {} - Offset: {}", 
                         notificacion.getId(), 
                         result.getRecordMetadata().offset());
            } else {
                log.error("❌ Error enviando notificación: {}", notificacion.getId(), ex);
            }
        });
    }
}
