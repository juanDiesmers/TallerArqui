package com.example.Taller_Tienda.Service;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class Notificaciones {

    private static final String TOPIC = "proveedorA_notificaciones";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void enviarNotificacion(String mensaje) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, mensaje);

            // Esperar la confirmación de Kafka (de manera bloqueante)
            SendResult<String, Object> result = future.get();

            RecordMetadata metadata = result.getRecordMetadata();

            System.out.println("✅ Mensaje confirmado por Kafka:");
            System.out.println("   • Topic: " + metadata.topic());
            System.out.println("   • Partición: " + metadata.partition());
            System.out.println("   • Offset: " + metadata.offset());
            System.out.println("   • Timestamp: " + metadata.timestamp());
            System.out.println("   • Contenido: " + mensaje);

        } catch (Exception e) {
            System.err.println("❌ Error al enviar mensaje a Kafka: " + e.getMessage());
        }
    }

}
