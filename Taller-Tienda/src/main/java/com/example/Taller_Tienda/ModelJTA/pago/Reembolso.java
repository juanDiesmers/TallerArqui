package com.example.Taller_Tienda.ModelJTA.pago;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "reembolso", schema = "pagos")
@Data
public class Reembolso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pago_id")
    private Pago pago;

    private Long montoCentavos;
    private String motivo;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private Instant creadoEn;

    // Getters y Setters
}
