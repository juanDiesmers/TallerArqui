package com.example.console;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RestClient {

  private final String baseUrl;
  private final HttpClient client;
  private final ObjectMapper mapper;

  public RestClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    this.mapper = new ObjectMapper();
  }

  /**
   * GET /api/products - Lista todos los productos
   */
  public List<ProductDTO> getAllProducts()
    throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl + "/products"))
      .GET()
      .header("Accept", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException(
        "HTTP " + response.statusCode() + ": " + response.body()
      );
    }

    JsonNode root = mapper.readTree(response.body());
    List<ProductDTO> products = new ArrayList<>();

    if (root.isArray()) {
      for (JsonNode node : root) {
        products.add(parseProduct(node));
      }
    }

    return products;
  }

  /**
   * GET /api/products/{id} - Obtiene un producto por ID
   */
  public ProductDTO getProduct(Long id)
    throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl + "/products/" + id))
      .GET()
      .header("Accept", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("HTTP " + response.statusCode());
    }

    return parseProduct(mapper.readTree(response.body()));
  }

  /**
   * POST /api/products - Crea un producto nuevo
   */
  public ProductDTO createProduct(
    String nombre,
    String descripcion,
    BigDecimal precio,
    int stock
  ) throws IOException, InterruptedException {
    String json = String.format(
      "{\"nombre\":\"%s\",\"descripcion\":\"%s\",\"precio\":%s,\"stock\":%d}",
      nombre,
      descripcion,
      precio,
      stock
    );

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl + "/products"))
      .POST(HttpRequest.BodyPublishers.ofString(json))
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200 && response.statusCode() != 201) {
      throw new IOException("HTTP " + response.statusCode());
    }

    return parseProduct(mapper.readTree(response.body()));
  }

  /**
   * PUT /api/products/{id} - Actualiza un producto
   */
  public ProductDTO updateProduct(
    Long id,
    String nombre,
    String descripcion,
    BigDecimal precio,
    int stock
  ) throws IOException, InterruptedException {
    String json = String.format(
      "{\"nombre\":\"%s\",\"descripcion\":\"%s\",\"precio\":%s,\"stock\":%d}",
      nombre,
      descripcion,
      precio,
      stock
    );

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl + "/products/" + id))
      .PUT(HttpRequest.BodyPublishers.ofString(json))
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("HTTP " + response.statusCode());
    }

    return parseProduct(mapper.readTree(response.body()));
  }

  /**
   * DELETE /api/products/{id} - Elimina un producto
   */
  public void deleteProduct(Long id) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl + "/products/" + id))
      .DELETE()
      .build();

    HttpResponse<Void> response = client.send(
      request,
      HttpResponse.BodyHandlers.discarding()
    );

    if (response.statusCode() != 204 && response.statusCode() != 200) {
      throw new IOException("HTTP " + response.statusCode());
    }
  }

  /**
   * GET /api/users/{userId}/orders - Obtiene órdenes recientes de un usuario
   */
  public List<OrderDTO> getUserOrders(Long userId, int page, int size)
    throws IOException, InterruptedException {
    String url = String.format(
      "%s/users/%d/orders?page=%d&size=%d",
      baseUrl,
      userId,
      page,
      size
    );

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(url))
      .GET()
      .header("Accept", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("HTTP " + response.statusCode());
    }

    JsonNode root = mapper.readTree(response.body());
    JsonNode content = root.get("content");

    List<OrderDTO> orders = new ArrayList<>();
    if (content != null && content.isArray()) {
      for (JsonNode node : content) {
        orders.add(parseOrder(node));
      }
    }

    return orders;
  }

  // ========== HELPERS ==========

  private ProductDTO parseProduct(JsonNode node) {
    ProductDTO p = new ProductDTO();
    p.id = node.get("id").asLong();
    p.nombre = node.get("nombre").asText();
    p.descripcion =
      node.has("descripcion") ? node.get("descripcion").asText() : null;
    p.precio = new BigDecimal(node.get("precio").asText());
    p.stock = node.get("stock").asInt();
    p.fechaCreacion =
      node.has("fechaCreacion") ? node.get("fechaCreacion").asText() : null;
    return p;
  }

  private OrderDTO parseOrder(JsonNode node) {
    OrderDTO o = new OrderDTO();
    o.id = node.get("id").asLong();
    o.code = node.get("code").asText();
    o.status = node.get("status").asText();
    o.totalCents = node.get("totalCents").asLong();
    o.createdAt = node.get("createdAt").asText();
    return o;
  }

  // ========== DTOs ==========

  public static class ProductDTO {

    public Long id;
    public String nombre;
    public String descripcion;
    public BigDecimal precio;
    public int stock;
    public String fechaCreacion;

    @Override
    public String toString() {
      return String.format(
        "Product[id=%d, nombre=%s, precio=%s, stock=%d]",
        id,
        nombre,
        precio,
        stock
      );
    }
  }

  public static class OrderDTO {

    public Long id;
    public String code;
    public String status;
    public Long totalCents;
    public String createdAt;

    @Override
    public String toString() {
      return String.format(
        "Order[id=%d, code=%s, status=%s, total=%d¢]",
        id,
        code,
        status,
        totalCents
      );
    }
  }
}
