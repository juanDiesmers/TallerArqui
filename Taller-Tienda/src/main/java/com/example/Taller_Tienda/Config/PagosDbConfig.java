package com.example.Taller_Tienda.Config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.tienda.repository.pagos",
    entityManagerFactoryRef = "pagosEntityManager", 
    transactionManagerRef = "transactionManager"
)
public class PagosDbConfig {
    
    @Autowired
    @Qualifier("pagosDataSource")
    private DataSource pagosDataSource;
    
    @Bean(name = "pagosEntityManager")
    public LocalContainerEntityManagerFactoryBean pagosEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pagosDataSource);
        em.setPackagesToScan(".com.example.Taller_Tienda.model.pago");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");
        
        em.setJpaPropertyMap(properties);
        return em;
    }
}