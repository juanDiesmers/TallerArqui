package com.example.console;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cliente RPC usando protocolo JSON-RPC 2.0
 *
 * Para usar este cliente, necesitarías agregar un endpoint RPC en tu backend.
 * Aquí muestro cómo sería el cliente. Si prefieres gRPC, puedo proporcionarte
 * esa implementación también.
 */
public class RpcClient {

  private final String baseUrl;
  private final HttpClient client;
  private final ObjectMapper mapper;
  private final AtomicLong requestId;

  public RpcClient() {
    this("http://localhost:8080/rpc");
  }

  public RpcClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    this.mapper = new ObjectMapper();
    this.requestId = new AtomicLong(1);
  }

  /**
   * Reserva stock de múltiples productos en una sola llamada (batch)
   */
  public BatchReserveResult batchReserve(long[] productIds, int[] quantities)
    throws IOException, InterruptedException {
    if (productIds.length != quantities.length) {
      throw new IllegalArgumentException("Arrays must have same length");
    }

    // Construir parámetros
    StringBuilder params = new StringBuilder("[");
    for (int i = 0; i < productIds.length; i++) {
      if (i > 0) params.append(",");
      params.append(
        String.format(
          "{\"productId\":%d,\"quantity\":%d}",
          productIds[i],
          quantities[i]
        )
      );
    }
    params.append("]");

    String jsonRpc = String.format(
      "{\"jsonrpc\":\"2.0\",\"method\":\"product.batchReserve\"," +
      "\"params\":{\"items\":%s},\"id\":%d}",
      params,
      requestId.getAndIncrement()
    );

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl))
      .POST(HttpRequest.BodyPublishers.ofString(jsonRpc))
      .header("Content-Type", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("RPC Error: HTTP " + response.statusCode());
    }

    JsonNode root = mapper.readTree(response.body());

    // Verificar si hay error
    if (root.has("error")) {
      JsonNode error = root.get("error");
      throw new RuntimeException(
        String.format(
          "RPC Error %d: %s",
          error.get("code").asInt(),
          error.get("message").asText()
        )
      );
    }

    JsonNode result = root.get("result");
    BatchReserveResult res = new BatchReserveResult();
    res.success = result.get("success").asInt();
    res.failed = result.get("failed").asInt();
    res.message = result.has("message") ? result.get("message").asText() : "";

    return res;
  }

  /**
   * Obtiene información de múltiples productos en una sola llamada
   */
  public ProductInfo[] batchGetProducts(long[] productIds)
    throws IOException, InterruptedException {
    StringBuilder ids = new StringBuilder("[");
    for (int i = 0; i < productIds.length; i++) {
      if (i > 0) ids.append(",");
      ids.append(productIds[i]);
    }
    ids.append("]");

    String jsonRpc = String.format(
      "{\"jsonrpc\":\"2.0\",\"method\":\"product.batchGet\"," +
      "\"params\":{\"ids\":%s},\"id\":%d}",
      ids,
      requestId.getAndIncrement()
    );

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl))
      .POST(HttpRequest.BodyPublishers.ofString(jsonRpc))
      .header("Content-Type", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("RPC Error: HTTP " + response.statusCode());
    }

    JsonNode root = mapper.readTree(response.body());

    if (root.has("error")) {
      JsonNode error = root.get("error");
      throw new RuntimeException(
        String.format(
          "RPC Error %d: %s",
          error.get("code").asInt(),
          error.get("message").asText()
        )
      );
    }

    JsonNode result = root.get("result");
    JsonNode products = result.get("products");

    ProductInfo[] infos = new ProductInfo[products.size()];
    for (int i = 0; i < products.size(); i++) {
      JsonNode p = products.get(i);
      ProductInfo info = new ProductInfo();
      info.id = p.get("id").asLong();
      info.nombre = p.get("nombre").asText();
      info.stock = p.get("stock").asInt();
      info.available = p.get("available").asBoolean();
      infos[i] = info;
    }

    return infos;
  }

  /**
   * Crea múltiples órdenes en una sola transacción
   */
  public BatchOrderResult batchCreateOrders(OrderRequest[] orders)
    throws IOException, InterruptedException {
    StringBuilder ordersJson = new StringBuilder("[");
    for (int i = 0; i < orders.length; i++) {
      if (i > 0) ordersJson.append(",");
      ordersJson.append(
        String.format(
          "{\"userId\":%d,\"items\":%s}",
          orders[i].userId,
          buildItemsJson(orders[i].items)
        )
      );
    }
    ordersJson.append("]");

    String jsonRpc = String.format(
      "{\"jsonrpc\":\"2.0\",\"method\":\"order.batchCreate\"," +
      "\"params\":{\"orders\":%s},\"id\":%d}",
      ordersJson,
      requestId.getAndIncrement()
    );

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl))
      .POST(HttpRequest.BodyPublishers.ofString(jsonRpc))
      .header("Content-Type", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("RPC Error: HTTP " + response.statusCode());
    }

    JsonNode root = mapper.readTree(response.body());

    if (root.has("error")) {
      JsonNode error = root.get("error");
      throw new RuntimeException(
        String.format(
          "RPC Error %d: %s",
          error.get("code").asInt(),
          error.get("message").asText()
        )
      );
    }

    JsonNode result = root.get("result");
    BatchOrderResult res = new BatchOrderResult();
    res.created = result.get("created").asInt();
    res.failed = result.get("failed").asInt();

    return res;
  }

  /**
   * Realiza múltiples operaciones en una sola llamada RPC batch
   */
  public void executeBatch(String[] methods, String[] paramsJson)
    throws IOException, InterruptedException {
    if (methods.length != paramsJson.length) {
      throw new IllegalArgumentException("Arrays must have same length");
    }

    // Construir array de requests batch
    StringBuilder batch = new StringBuilder("[");
    for (int i = 0; i < methods.length; i++) {
      if (i > 0) batch.append(",");
      batch.append(
        String.format(
          "{\"jsonrpc\":\"2.0\",\"method\":\"%s\",\"params\":%s,\"id\":%d}",
          methods[i],
          paramsJson[i],
          requestId.getAndIncrement()
        )
      );
    }
    batch.append("]");

    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl))
      .POST(HttpRequest.BodyPublishers.ofString(batch.toString()))
      .header("Content-Type", "application/json")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new IOException("RPC Batch Error: HTTP " + response.statusCode());
    }

    // Procesar respuestas batch
    JsonNode responses = mapper.readTree(response.body());
    for (JsonNode resp : responses) {
      if (resp.has("error")) {
        System.err.println("Batch item error: " + resp.get("error"));
      }
    }
  }

  // ========== HELPERS ==========

  private String buildItemsJson(OrderItem[] items) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < items.length; i++) {
      if (i > 0) sb.append(",");
      sb.append(
        String.format(
          "{\"productId\":%d,\"quantity\":%d}",
          items[i].productId,
          items[i].quantity
        )
      );
    }
    sb.append("]");
    return sb.toString();
  }

  // ========== DTOs ==========

  public static class BatchReserveResult {

    public int success;
    public int failed;
    public String message;

    @Override
    public String toString() {
      return String.format(
        "BatchReserve[success=%d, failed=%d, msg=%s]",
        success,
        failed,
        message
      );
    }
  }

  public static class ProductInfo {

    public long id;
    public String nombre;
    public int stock;
    public boolean available;

    @Override
    public String toString() {
      return String.format(
        "ProductInfo[id=%d, nombre=%s, stock=%d, available=%b]",
        id,
        nombre,
        stock,
        available
      );
    }
  }

  public static class OrderRequest {

    public long userId;
    public OrderItem[] items;

    public OrderRequest(long userId, OrderItem[] items) {
      this.userId = userId;
      this.items = items;
    }
  }

  public static class OrderItem {

    public long productId;
    public int quantity;

    public OrderItem(long productId, int quantity) {
      this.productId = productId;
      this.quantity = quantity;
    }
  }

  public static class BatchOrderResult {

    public int created;
    public int failed;

    @Override
    public String toString() {
      return String.format(
        "BatchOrder[created=%d, failed=%d]",
        created,
        failed
      );
    }
  }
}
