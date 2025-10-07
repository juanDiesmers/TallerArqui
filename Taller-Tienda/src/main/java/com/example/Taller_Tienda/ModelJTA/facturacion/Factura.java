package com.example.Taller_Tienda.ModelJTA.facturacion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "factura", schema = "facturacion")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Column(name = "cliente_id")
    private Long clienteId;

    private java.time.Instant fecha;
    
    @Column(name = "total_centavos")
    private Long totalCentavos;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private java.util.List<FacturaItem> items;

    public enum EstadoFactura {
        BORRADOR, EMITIDA, PAGADA, ANULADA
    }


}
