package com.example.Taller_Tienda.RepositoryJTA.factura;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Taller_Tienda.ModelJTA.facturacion.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}
