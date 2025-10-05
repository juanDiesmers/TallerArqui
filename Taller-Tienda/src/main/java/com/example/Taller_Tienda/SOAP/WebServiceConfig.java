package com.example.Taller_Tienda.SOAP;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

// ✅ Importa las clases JAXB generadas (según tu grep)
import com.example.taller.tienda.xsd.GetProductoRequest;
import com.example.taller.tienda.xsd.GetProductoResponse;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    /** Debe coincidir con el targetNamespace del XSD */
    public static final String NS = "http://example.com/taller/tienda";

    /** Registra el servlet de Spring-WS en /ws/* */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext ctx) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(ctx);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /** Carga el XSD desde classpath */
    @Bean
    public XsdSchema productoSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/producto.xsd"));
    }

    /** Publica el WSDL en /ws/productos.wsdl (el nombre del bean define el nombre del WSDL) */
    @Bean(name = "productos")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema productoSchema) {
        DefaultWsdl11Definition def = new DefaultWsdl11Definition();
        def.setPortTypeName("ProductosPort");
        def.setLocationUri("/ws");
        def.setTargetNamespace(NS); // 👈 igual al XSD
        def.setSchema(productoSchema);
        return def;
    }

    /** Marshaller JAXB enlazando por clases (evita errores de contextPath) */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller m = new Jaxb2Marshaller();
        m.setClassesToBeBound(
            GetProductoRequest.class,
            GetProductoResponse.class
        );
        return m;
    }
}
