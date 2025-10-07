package com.example.Taller_Tienda.RepositoryJTA.factura;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Taller_Tienda.ModelJTA.facturacion.FacturaItem;

public interface FacturaItemRepository extends JpaRepository<FacturaItem, Long> {
}
