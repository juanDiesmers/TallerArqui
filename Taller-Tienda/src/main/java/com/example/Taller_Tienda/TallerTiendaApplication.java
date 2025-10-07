package com.example.Taller_Tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.example.Taller_Tienda.Repository")
@EntityScan(basePackages = {"com.example.Taller_Tienda.Model", "com.example.Taller_Tienda.ModelJTA"})
@EnableTransactionManagement
public class TallerTiendaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TallerTiendaApplication.class, args);
    }
}