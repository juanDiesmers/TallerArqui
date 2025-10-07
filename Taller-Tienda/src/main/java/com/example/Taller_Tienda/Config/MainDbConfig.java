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
        basePackages = {"com.example.Taller_Tienda.Repository.order", "com.example.Taller_Tienda.Repository.inventario", "com.example.Taller_Tienda.Repository.user", "com.example.Taller_Tienda.Repository.pagos", "com.example.Taller_Tienda.Repository.facturacion"},
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class MainDbConfig {
    
    @Autowired
    @Qualifier("orderDataSource")
    private DataSource orderDataSource;
    
    
    @Bean(name = "orderEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean facturacionEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(orderDataSource);
        em.setPackagesToScan("com.example.Taller_Tienda.Model");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");
        properties.put("hibernate.show_sql", true);


        em.setJpaPropertyMap(properties);
        return em;
    }

   /* @Bean(name = "facturacionTransactionManager")
    public PlatformTransactionManager facturacionTransactionManager(
            @Qualifier("facturacionEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }*/
}