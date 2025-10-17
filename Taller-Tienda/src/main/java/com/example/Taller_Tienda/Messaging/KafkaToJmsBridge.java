package com.example.Taller_Tienda.Messaging;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

// IMPORTA JAKARTA, NO javax:
import jakarta.jms.Connection;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

@Component
public class KafkaToJmsBridge {

  @Value("${wildfly.jms.host:wildfly}")
  private String jmsHost;

  @Value("${wildfly.jms.port:5445}")
  private int jmsPort;

  @Value("${wildfly.jms.username:}")
  private String jmsUser;

  @Value("${wildfly.jms.password:}")
  private String jmsPass;

  @Value("${wildfly.jms.queue:jms.queue.orderEvents}")
  private String queueName;

  private Connection connection;
  private Session session;
  private MessageProducer producer;

  @PostConstruct
  public void init() throws Exception {
    String url = "tcp://" + jmsHost + ":" + jmsPort;

    ActiveMQConnectionFactory cf = (jmsUser == null || jmsUser.isBlank())
        ? new ActiveMQConnectionFactory(url)
        : new ActiveMQConnectionFactory(jmsUser, jmsPass, url);

    this.connection = cf.createConnection();
    this.connection.start();

    this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Queue queue = session.createQueue(queueName);
    this.producer = session.createProducer(queue);

    System.out.println("✅ Conexión JMS (Jakarta) establecida con WildFly en " + url);
  }

  @PreDestroy
  public void close() throws Exception {
    if (producer != null) producer.close();
    if (session != null) session.close();
    if (connection != null) connection.close();
  }

  @KafkaListener(topics = "proveedorA_notificaciones")
  public void onKafka(@Payload String payload) throws Exception {
    System.out.println("📩 Kafka -> " + payload);
    TextMessage msg = session.createTextMessage(payload);
    producer.send(msg);
    System.out.println("➡️  Enviado a JMS (WildFly)");
  }
}
