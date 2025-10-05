package com.example.Taller_Tienda.Config;

import java.sql.Connection;
import java.util.HashMap;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration

@EnableJpaRepositories(
        basePackages = "com.example.Taller_Tienda.Repository.inventario",
        entityManagerFactoryRef = "inventarioEntityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class InventarioDbConfig {
    
    @Autowired
    @Qualifier("inventarioDataSource")
    private DataSource inventarioDataSource;
    
    @Primary
    @Bean(name = "inventarioEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean inventarioEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(inventarioDataSource);
        em.setPackagesToScan("com.example.Taller_Tienda.Model");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
     //   properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");
        properties.put("hibernate.show_sql", true);
//        properties.put(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:mysql://mysql:3306/inventario?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
//        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
//        properties.put(AvailableSettings.JAKARTA_JDBC_USER, "root");
//        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, "MiClaveSegura123!");
        properties.put("hibernate.connection.url", "jdbc:mysql://mysql:3306/inventario?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
        properties.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.put("hibernate.connection.username", "root");
        properties.put("hibernate.connection.password", "MiClaveSegura123!");
        properties.put("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_READ_COMMITTED));
        
        em.setJpaPropertyMap(properties);
        return em;
    }

    /*@Bean(name = "inventarioTransactionManager")
    public PlatformTransactionManager inventarioTransactionManager(
            @Qualifier("inventarioEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }*/
}
