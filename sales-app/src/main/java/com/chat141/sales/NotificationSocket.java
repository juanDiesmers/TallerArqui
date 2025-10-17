package com.chat141.sales;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/notifications")
public class NotificationSocket {
  private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

  @OnOpen public void onOpen(Session s){ sessions.add(s); }
  @OnClose public void onClose(Session s){ sessions.remove(s); }
  @OnError public void onError(Session s, Throwable t){ /* log as needed */ }

  public static void broadcast(String payload) {
    for (Session s : sessions) {
      if (s.isOpen()) {
        s.getAsyncRemote().sendText(payload);
      }
    }
  }
}
