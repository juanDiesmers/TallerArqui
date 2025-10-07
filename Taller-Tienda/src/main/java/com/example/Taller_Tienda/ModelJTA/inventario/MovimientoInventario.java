package com.example.Taller_Tienda.ModelJTA.inventario;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "movimiento_inventario", schema = "inventario")
@Data
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    private int cantidad;
    private String referencia;
    private String notas;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private Instant creadoEn;

    public enum TipoMovimiento {
        ENTRADA, SALIDA, AJUSTE
    }

}
