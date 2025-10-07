package com.example.Taller_Tienda.ModelJTA.facturacion;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "cliente", schema = "facturacion")
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nombre;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private Instant creadoEn;

    @OneToMany
    @JoinColumn(name = "cliente_id")
    private List<Factura> facturas;

    // Getters y Setters
}
