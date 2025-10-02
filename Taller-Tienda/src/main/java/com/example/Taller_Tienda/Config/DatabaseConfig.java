package com.example.Taller_Tienda.Config;


import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;



@Configuration
public class DatabaseConfig {
    
    @Bean(name = "inventarioDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.inventario")
    public DataSource inventarioDataSource() {
        return new AtomikosDataSourceBean();
    }
    
    @Bean(name = "facturacionDataSource") 
    @ConfigurationProperties(prefix = "spring.datasource.facturacion")
    public DataSource facturacionDataSource() {
        return new AtomikosDataSourceBean();
    }
    
    @Bean(name = "pagosDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pagos")
    public DataSource pagosDataSource() {
        return new AtomikosDataSourceBean();
    }
}