package com.example.Taller_Tienda.ModelJTA.inventario;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "almacen", schema = "inventario")
@Data
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ubicacion;

    @OneToMany(mappedBy = "almacen")
    private List<MovimientoInventario> movimientos;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}