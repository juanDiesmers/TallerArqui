package com.example.Taller_Tienda.SOAP;

import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Service.ProductService;

import com.example.taller.tienda.xsd.GetProductoRequest;
import com.example.taller.tienda.xsd.GetProductoResponse;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;

@Endpoint
public class ProductSoapEndpoint {

  private static final String NS = WebServiceConfig.NS;

  private final ProductService service;

  public ProductSoapEndpoint(ProductService service) {
    this.service = service;
  }

  @PayloadRoot(namespace = NS, localPart = "GetProductoRequest")
  @ResponsePayload
  public GetProductoResponse getProduct(@RequestPayload GetProductoRequest req) {
    long id = req.getId();
    Product p = service.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));

    GetProductoResponse resp = new GetProductoResponse();
    resp.setId(p.getId());
    resp.setNombre(p.getNombre() == null ? "" : p.getNombre());
    resp.setPrecio(p.getPrecio() == null ? BigDecimal.ZERO : p.getPrecio());
    return resp;
  }
}
