
package com.example.Taller_Tienda.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Taller_Tienda.Service.TestJTATransactionService;

@RestController
public class TestJTAController {

    private final TestJTATransactionService orderService;

    public TestJTAController(TestJTATransactionService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/test/distributed")
    public String testRollback() {
        try {
            orderService.processOrderRollback();
            return "Proceso completado exitosamente";
        } catch (Exception e) {
            return "Rollback exitoso: " + e.getMessage();
        }
    }

}

