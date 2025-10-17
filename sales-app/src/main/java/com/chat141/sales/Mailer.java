package com.chat141.sales;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@ApplicationScoped
public class Mailer {

  @Resource(lookup = "java:/Mail")
  private Session mailSession;

  public void send(String to, String subject, String text) {
    try {
      MimeMessage m = new MimeMessage(mailSession);
      m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
      m.setSubject(subject, java.nio.charset.StandardCharsets.UTF_8.name());
      m.setText(text, java.nio.charset.StandardCharsets.UTF_8.name());
      Transport.send(m);
    } catch (MessagingException e) {
      throw new RuntimeException("Mail error", e);
    }
  }
}
