package com.example.Taller_Tienda.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }


    @GetMapping
    public List<Product> all() { return service.findAll(); }


    @GetMapping("/{id}")
    public ResponseEntity<Product> byId(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Product create(@RequestBody Product p) { return service.save(p); }


    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product p) {
        return service.findById(id).map(existing -> {
            // Aquí deberías actualizar los campos
            existing.setNombre(p.getNombre());
            existing.setDescripcion(p.getDescripcion());
            existing.setPrecio(p.getPrecio());
            existing.setFechaCreacion(p.getFechaCreacion());

            return ResponseEntity.ok(service.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
