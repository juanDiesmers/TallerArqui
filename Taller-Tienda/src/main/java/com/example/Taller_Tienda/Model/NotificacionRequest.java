package com.example.Taller_Tienda.Model;

public class NotificacionRequest {
    private String usuarioId;
    private String mensaje;
    private String tipo;
    
    public NotificacionRequest() {}
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}