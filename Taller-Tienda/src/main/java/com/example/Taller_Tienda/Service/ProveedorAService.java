package com.example.Taller_Tienda.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.Taller_Tienda.ModelProveedorA.Factura;

@Service
public class ProveedorAService {

    private static final String TOPIC = "proveedorA_notificaciones";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Envía una notificación simple de texto
    public void enviarNotificacion(String mensaje) {
        kafkaTemplate.send(TOPIC, mensaje);
        System.out.println("✅ Notificación enviada a Kafka: " + mensaje);
    }

    // Envía una factura completa al topic de Kafka
    public void enviarFactura(Factura factura) {
        kafkaTemplate.send(TOPIC, factura);
        System.out.println("✅ Factura enviada a Kafka: " + factura.getNumeroFactura());
    }
}
