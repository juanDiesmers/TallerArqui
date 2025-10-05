package com.example.Taller_Tienda.Config;

import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.Taller_Tienda.Repository.order",
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
//        properties.put(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:mysql://mysql:3306/facturacion?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
//        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
//        properties.put(AvailableSettings.JAKARTA_JDBC_USER, "root");
//        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, "MiClaveSegura123!");
        properties.put("hibernate.connection.url", "jdbc:mysql://mysql:3306/tallerAR?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
        properties.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.put("hibernate.connection.username", "root");
        properties.put("hibernate.connection.password", "MiClaveSegura123!");
        properties.put("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_READ_COMMITTED));


        em.setJpaPropertyMap(properties);
        return em;
    }

   /* @Bean(name = "facturacionTransactionManager")
    public PlatformTransactionManager facturacionTransactionManager(
            @Qualifier("facturacionEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }*/
}