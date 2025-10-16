package com.example.Taller_Tienda.ModelProveedorA;

import java.time.LocalDate;
import java.util.List;

public class Factura {
    private Long id;
    private String numeroFactura;
    private String cliente;
    private LocalDate fechaEmision;
    private double total;
    private List<ProductoA> productos;

    // Constructor vacío
    public Factura() {
    }

    // Constructor con parámetros
    public Factura(Long id, String numeroFactura, String cliente, LocalDate fechaEmision, double total, List<ProductoA> productos) {
        this.id = id;
        this.numeroFactura = numeroFactura;
        this.cliente = cliente;
        this.fechaEmision = fechaEmision;
        this.total = total;
        this.productos = productos;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ProductoA> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoA> productos) {
        this.productos = productos;
    }
}
