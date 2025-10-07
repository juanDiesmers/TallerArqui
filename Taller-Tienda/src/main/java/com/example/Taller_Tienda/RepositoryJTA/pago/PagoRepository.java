
package com.example.Taller_Tienda.RepositoryJTA.pago;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Taller_Tienda.ModelJTA.pago.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
