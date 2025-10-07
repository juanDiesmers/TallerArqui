package com.example.Taller_Tienda.ModelJTA.pago;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pago", schema = "pagos")
@Data
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    /* @ManyToOne(optional = false)
    @JoinColumn(name = "metodo_id")
    private MetodoPago metodo; */

    @Column(name = "monto_centavos")
    private Long montoCentavos;

    @Column(name = "moneda", length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @Column(name = "referencia_externa")
    private String referenciaExterna;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private Instant creadoEn;

    public enum EstadoPago {
        PENDIENTE, APLICADO, FALLIDO, REEMBOLSADO
    }

}
