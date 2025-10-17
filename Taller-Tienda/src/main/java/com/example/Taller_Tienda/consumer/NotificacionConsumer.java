package com.example.Taller_Tienda.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.Taller_Tienda.Model.Notificacion;
import com.example.Taller_Tienda.Service.NotificacionService;

/**
 * MDB equivalente en Spring Boot
 * Este componente escucha el topic de Kafka y procesa las notificaciones
 */
@Component
public class NotificacionConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(NotificacionConsumer.class);
    
    @Autowired
    private NotificacionService notificacionService;
    
    /**
     * Método listener que consume mensajes del topic de notificaciones
     * 
     * @KafkaListener: Marca este método como consumidor de Kafka (equivalente a @MessageDriven en EJB)
     * - topics: Topic(s) de Kafka a escuchar
     * - groupId: Grupo de consumidores (permite escalabilidad)
     * - containerFactory: Factory configurado anteriormente
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.notificaciones}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirNotificacion(
            @Payload Notificacion notificacion,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        log.info("=== Mensaje recibido ===");
        log.info("Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
        log.info("Notificación: {}", notificacion);
        
        procesarNotificacion(notificacion);
            
        log.info("Notificación procesada exitosamente: {}", notificacion.getId());
            
    }
    
    /**
     * Alternativa con control manual de commit (para casos más críticos)
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.notificaciones-criticas}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirNotificacionCritica(
            ConsumerRecord<String, Notificacion> record,
            Acknowledgment acknowledgment) {
        
        Notificacion notificacion = record.value();
        
        log.info("Procesando notificación crítica: {}", notificacion.getId());
        
        try {
            procesarNotificacion(notificacion);
            
            // Commit manual del offset solo si todo salió bien
            acknowledgment.acknowledge();
            
            log.info("Notificación crítica confirmada: {}", notificacion.getId());
            
        } catch (Exception e) {
            log.error("Error en notificación crítica: {}", notificacion.getId(), e);
            // No hacemos acknowledge, el mensaje se reprocesará
        }
    }
    
    /**
     * Lógica de procesamiento de la notificación
     */
    private void procesarNotificacion(Notificacion notificacion) {
         notificacionService.enviarEmail(notificacion);   
    }

}