package com.chat141.sales;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@MessageDriven(activationConfig = {
  @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/orderEvents"),
  @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NotificationListener implements MessageListener {

  @Inject Mailer mailer;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void onMessage(Message message) {
    try {
      OrderEvent ev;
      if (message instanceof TextMessage tm) {
        ev = mapper.readValue(tm.getText(), OrderEvent.class);
      } else if (message instanceof ObjectMessage om && om.getObject() instanceof OrderEvent e) {
        ev = e;
      } else {
        throw new IllegalArgumentException("Unsupported message type: " + message);
      }

      switch (ev.getType()) {
        case "OrderCreated" -> mailer.send("cliente@example.com",
            "Pedido #" + ev.getOrderId(), "¡Gracias por tu compra!");
        case "OrderPaid" -> mailer.send("proveedor@example.com",
            "Pedido pagado #" + ev.getOrderId(), "El pedido fue marcado como pagado.");
        default -> {}
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
