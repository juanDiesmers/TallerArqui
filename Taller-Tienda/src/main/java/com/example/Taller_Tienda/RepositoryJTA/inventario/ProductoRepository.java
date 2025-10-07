package com.example.Taller_Tienda.RepositoryJTA.inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Taller_Tienda.ModelJTA.inventario.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}