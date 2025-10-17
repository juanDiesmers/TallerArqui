package com.example.Taller_Tienda.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notificacion {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("usuario_id")
    private String usuarioId;
    
    @JsonProperty("mensaje")
    private String mensaje;
    
    @JsonProperty("tipo")
    private String tipo; 
    @JsonProperty("fecha")
    private LocalDateTime fecha;
    
    @JsonProperty("leida")
    private boolean leida;
    
    // Constructores
    public Notificacion() {}
    
    public Notificacion(String id, String usuarioId, String mensaje, String tipo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
        this.leida = false;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    
    @Override
    public String toString() {
        return "Notificacion{" +
                "id='" + id + '\'' +
                ", usuarioId='" + usuarioId + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fecha=" + fecha +
                ", leida=" + leida +
                '}';
    }
}
