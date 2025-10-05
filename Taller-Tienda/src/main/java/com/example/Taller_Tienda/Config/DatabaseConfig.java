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
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setUniqueResourceName("myDataSourceInventario");
        xaDataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        return xaDataSource;
    }

    @Bean(name = "orderDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.order")
    public DataSource orderDataSource() {
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setUniqueResourceName("myDataSourceOrder");
        xaDataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        return xaDataSource;
    }
    
    @Bean(name = "facturacionDataSource") 
    @ConfigurationProperties(prefix = "spring.datasource.facturacion")
    public DataSource facturacionDataSource() {
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setUniqueResourceName("myDataSourceFacturacion");
        xaDataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        return xaDataSource;
    }
    
    @Bean(name = "pagosDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pagos")
    public DataSource pagosDataSource() {
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setUniqueResourceName("myDataSourcePagos");
        xaDataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        return xaDataSource;
    }
}