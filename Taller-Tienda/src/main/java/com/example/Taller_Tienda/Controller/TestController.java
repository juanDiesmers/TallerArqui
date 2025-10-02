package com.example.Taller_Tienda.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.Service.OrderService;  

@RestController
public class TestController {

    private final OrderService orderService;

    public TestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/test/distributed")
    public String testDistributedTx() {
        try {
            orderService.processOrderRollback();
            return "Proceso completado exitosamente";
        } catch (Exception e) {
            return "Rollback exitoso: " + e.getMessage();
        }
    }
}
 
