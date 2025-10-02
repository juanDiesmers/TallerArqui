package com.example.Taller_Tienda.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;


@Configuration
public class TransactionManagerConfig {
    
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return transactionManager;
    }
}