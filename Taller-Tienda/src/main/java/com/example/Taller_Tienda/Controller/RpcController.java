package com.example.Taller_Tienda.Controller;

import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador RPC que implementa JSON-RPC 2.0
 * Endpoint: POST /rpc
 */
@RestController
@RequestMapping("/rpc")
public class RpcController {

  private final ProductService productService;
  private final ObjectMapper mapper;

  public RpcController(ProductService productService) {
    this.productService = productService;
    this.mapper = new ObjectMapper();
  }

  /**
   * Endpoint principal RPC que maneja requests individuales o batch
   */
  @PostMapping
  public Object handleRpc(@RequestBody JsonNode request) {
    // Verificar si es batch (array) o single request
    if (request.isArray()) {
      return handleBatchRequest(request);
    } else {
      return handleSingleRequest(request);
    }
  }

  /**
   * Maneja múltiples requests RPC en una sola llamada
   */
  private ArrayNode handleBatchRequest(JsonNode requests) {
    ArrayNode responses = mapper.createArrayNode();

    for (JsonNode req : requests) {
      ObjectNode response = handleSingleRequest(req);
      responses.add(response);
    }

    return responses;
  }

  /**
   * Maneja un solo request RPC
   */
  private ObjectNode handleSingleRequest(JsonNode request) {
    ObjectNode response = mapper.createObjectNode();
    response.put("jsonrpc", "2.0");

    // Validar versión JSON-RPC
    if (
      !request.has("jsonrpc") || !request.get("jsonrpc").asText().equals("2.0")
    ) {
      return buildError(
        response,
        null,
        -32600,
        "Invalid Request",
        "jsonrpc must be '2.0'"
      );
    }

    // Obtener ID del request
    JsonNode idNode = request.get("id");
    if (idNode != null) {
      if (idNode.isNumber()) {
        response.put("id", idNode.asLong());
      } else {
        response.put("id", idNode.asText());
      }
    } else {
      response.putNull("id");
    }

    // Validar método
    if (!request.has("method")) {
      return buildError(
        response,
        idNode,
        -32600,
        "Invalid Request",
        "method is required"
      );
    }

    String method = request.get("method").asText();
    JsonNode params = request.has("params")
      ? request.get("params")
      : mapper.createObjectNode();

    // Dispatch del método
    try {
      ObjectNode result = dispatchMethod(method, params);
      response.set("result", result);
    } catch (RpcException e) {
      return buildError(response, idNode, e.code, e.getMessage(), e.data);
    } catch (Exception e) {
      return buildError(
        response,
        idNode,
        -32603,
        "Internal error",
        e.getMessage()
      );
    }

    return response;
  }

  /**
   * Despacha el método RPC solicitado
   */
  private ObjectNode dispatchMethod(String method, JsonNode params)
    throws RpcException {
    return switch (method) {
      case "product.batchReserve" -> batchReserve(params);
      case "product.batchGet" -> batchGetProducts(params);
      case "product.getAll" -> getAllProducts(params);
      case "product.getById" -> getProductById(params);
      case "product.checkStock" -> checkStock(params);
      case "order.batchCreate" -> batchCreateOrders(params);
      default -> throw new RpcException(
        -32601,
        "Method not found: " + method,
        null
      );
    };
  }

  // ========== MÉTODOS RPC ==========

  /**
   * product.batchReserve - Reserva stock de múltiples productos
   */
  private ObjectNode batchReserve(JsonNode params) throws RpcException {
    if (!params.has("items") || !params.get("items").isArray()) {
      throw new RpcException(
        -32602,
        "Invalid params",
        "'items' array is required"
      );
    }

    ArrayNode items = (ArrayNode) params.get("items");
    int success = 0;
    int failed = 0;
    List<String> errors = new ArrayList<>();

    for (JsonNode item : items) {
      try {
        long productId = item.get("productId").asLong();
        int quantity = item.get("quantity").asInt();

        boolean reserved = productService.reserveStock(productId, quantity);
        if (reserved) {
          success++;
        } else {
          failed++;
          errors.add(
            String.format("Product %d: insufficient stock", productId)
          );
        }
      } catch (Exception e) {
        failed++;
        errors.add(e.getMessage());
      }
    }

    ObjectNode result = mapper.createObjectNode();
    result.put("success", success);
    result.put("failed", failed);
    if (!errors.isEmpty()) {
      result.put("message", String.join("; ", errors));
    }

    return result;
  }

  /**
   * product.batchGet - Obtiene información de múltiples productos
   */
  private ObjectNode batchGetProducts(JsonNode params) throws RpcException {
    if (!params.has("ids") || !params.get("ids").isArray()) {
      throw new RpcException(
        -32602,
        "Invalid params",
        "'ids' array is required"
      );
    }

    ArrayNode ids = (ArrayNode) params.get("ids");
    ArrayNode products = mapper.createArrayNode();

    for (JsonNode idNode : ids) {
      long id = idNode.asLong();
      Optional<Product> productOpt = productService.findById(id);

      if (productOpt.isPresent()) {
        Product p = productOpt.get();
        ObjectNode productNode = mapper.createObjectNode();
        productNode.put("id", p.getId());
        productNode.put("nombre", p.getNombre());
        productNode.put("stock", p.getStock());
        productNode.put("available", p.getStock() > 0);
        productNode.put("precio", p.getPrecio());
        products.add(productNode);
      }
    }

    ObjectNode result = mapper.createObjectNode();
    result.set("products", products);
    result.put("count", products.size());

    return result;
  }

  /**
   * product.getAll - Lista todos los productos
   */
  private ObjectNode getAllProducts(JsonNode params) {
    List<Product> allProducts = productService.findAll();
    ArrayNode products = mapper.createArrayNode();

    for (Product p : allProducts) {
      ObjectNode node = mapper.createObjectNode();
      node.put("id", p.getId());
      node.put("nombre", p.getNombre());
      node.put("descripcion", p.getDescripcion());
      node.put("precio", p.getPrecio());
      node.put("stock", p.getStock());
      products.add(node);
    }

    ObjectNode result = mapper.createObjectNode();
    result.set("products", products);
    result.put("count", products.size());

    return result;
  }

  /**
   * product.getById - Obtiene un producto por ID
   */
  private ObjectNode getProductById(JsonNode params) throws RpcException {
    if (!params.has("id")) {
      throw new RpcException(-32602, "Invalid params", "'id' is required");
    }

    long id = params.get("id").asLong();
    Optional<Product> productOpt = productService.findById(id);

    if (productOpt.isEmpty()) {
      throw new RpcException(-32000, "Product not found", "ID: " + id);
    }

    Product p = productOpt.get();
    ObjectNode result = mapper.createObjectNode();
    result.put("id", p.getId());
    result.put("nombre", p.getNombre());
    result.put("descripcion", p.getDescripcion());
    result.put("precio", p.getPrecio());
    result.put("stock", p.getStock());

    return result;
  }

  /**
   * product.checkStock - Verifica disponibilidad de stock
   */
  private ObjectNode checkStock(JsonNode params) throws RpcException {
    if (!params.has("productId") || !params.has("quantity")) {
      throw new RpcException(
        -32602,
        "Invalid params",
        "'productId' and 'quantity' are required"
      );
    }

    long productId = params.get("productId").asLong();
    int quantity = params.get("quantity").asInt();

    Optional<Product> productOpt = productService.findById(productId);
    if (productOpt.isEmpty()) {
      throw new RpcException(-32000, "Product not found", "ID: " + productId);
    }

    Product p = productOpt.get();
    boolean available = p.getStock() >= quantity;

    ObjectNode result = mapper.createObjectNode();
    result.put("productId", productId);
    result.put("requestedQuantity", quantity);
    result.put("availableStock", p.getStock());
    result.put("available", available);

    return result;
  }

  /**
   * order.batchCreate - Crea múltiples órdenes (stub)
   */
  private ObjectNode batchCreateOrders(JsonNode params) throws RpcException {
    if (!params.has("orders") || !params.get("orders").isArray()) {
      throw new RpcException(
        -32602,
        "Invalid params",
        "'orders' array is required"
      );
    }

    // Aquí implementarías la lógica real de creación de órdenes
    // Por ahora, retornamos un resultado simulado

    ArrayNode orders = (ArrayNode) params.get("orders");

    ObjectNode result = mapper.createObjectNode();
    result.put("created", orders.size());
    result.put("failed", 0);
    result.put("message", "Orders created successfully (stub)");

    return result;
  }

  // ========== HELPERS ==========

  private ObjectNode buildError(
    ObjectNode response,
    JsonNode id,
    int code,
    String message,
    String data
  ) {
    if (id != null) {
      if (id.isNumber()) {
        response.put("id", id.asLong());
      } else {
        response.put("id", id.asText());
      }
    } else {
      response.putNull("id");
    }

    ObjectNode error = mapper.createObjectNode();
    error.put("code", code);
    error.put("message", message);
    if (data != null) {
      error.put("data", data);
    }

    response.set("error", error);
    return response;
  }

  /**
   * Excepción personalizada para errores RPC
   */
  static class RpcException extends Exception {

    final int code;
    final String data;

    RpcException(int code, String message, String data) {
      super(message);
      this.code = code;
      this.data = data;
    }
  }
}
