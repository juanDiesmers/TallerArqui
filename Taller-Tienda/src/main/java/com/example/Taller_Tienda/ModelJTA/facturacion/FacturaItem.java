package com.example.Taller_Tienda.ModelJTA.facturacion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "factura_item", schema = "facturacion")
public class FacturaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación interna (misma DB)
    @ManyToOne(optional = false)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    // 🔹 Referencia al producto de otra base mediante ID
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    private int cantidad;

    @Column(name = "precio_unitario_centavos")
    private Long precioUnitarioCentavos;
}
