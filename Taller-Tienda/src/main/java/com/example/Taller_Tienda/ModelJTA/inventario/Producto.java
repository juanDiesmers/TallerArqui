package com.example.Taller_Tienda.ModelJTA.inventario;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "producto", schema = "inventario")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private int stock;

    @Column(name = "fecha_creacion", updatable = false, insertable = false)
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "producto")
    private List<MovimientoInventario> movimientos;

   
}
