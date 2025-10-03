package com.example.console;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Cliente SOAP manual para el servicio de productos.
 * Se comunica con el endpoint /ws usando mensajes SOAP XML.
 */
public class SoapClient {

  private static final String NAMESPACE = "http://example.com/taller/tienda";
  private static final String SOAP_ENV = "http://schemas.xmlsoap.org/soap/envelope/";

  private final String baseUrl;
  private final HttpClient client;

  public SoapClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.client =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
  }

  /**
   * Obtiene un producto por ID usando SOAP
   */
  public ProductoSOAP getProducto(Long id) throws Exception {
    // Construir mensaje SOAP
    String soapMessage = buildGetProductoRequest(id);

    // Enviar petición
    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create(baseUrl))
      .POST(HttpRequest.BodyPublishers.ofString(soapMessage))
      .header("Content-Type", "text/xml; charset=utf-8")
      .header("SOAPAction", "")
      .build();

    HttpResponse<String> response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
      throw new RuntimeException(
        "SOAP Error: HTTP " + response.statusCode() + "\n" + response.body()
      );
    }

    // Parsear respuesta
    return parseGetProductoResponse(response.body());
  }

  /**
   * Construye el mensaje SOAP para GetProductoRequest
   */
  private String buildGetProductoRequest(Long id) {
    return String.format(
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<soapenv:Envelope xmlns:soapenv=\"%s\" xmlns:tien=\"%s\">" +
      "  <soapenv:Header/>" +
      "  <soapenv:Body>" +
      "    <tien:GetProductoRequest>" +
      "      <tien:id>%d</tien:id>" +
      "    </tien:GetProductoRequest>" +
      "  </soapenv:Body>" +
      "</soapenv:Envelope>",
      SOAP_ENV,
      NAMESPACE,
      id
    );
  }

  /**
   * Parsea la respuesta SOAP de GetProductoResponse
   */
  private ProductoSOAP parseGetProductoResponse(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    Document doc = factory
      .newDocumentBuilder()
      .parse(new InputSource(new StringReader(xml)));

    // Buscar el elemento GetProductoResponse
    NodeList nodes = doc.getElementsByTagNameNS(
      NAMESPACE,
      "GetProductoResponse"
    );
    if (nodes.getLength() == 0) {
      // Buscar Fault si hay error
      NodeList faults = doc.getElementsByTagNameNS(SOAP_ENV, "Fault");
      if (faults.getLength() > 0) {
        Element fault = (Element) faults.item(0);
        String faultString = getTextContent(fault, "faultstring");
        throw new RuntimeException("SOAP Fault: " + faultString);
      }
      throw new RuntimeException(
        "No GetProductoResponse found in SOAP response"
      );
    }

    Element response = (Element) nodes.item(0);

    ProductoSOAP producto = new ProductoSOAP();
    producto.id = Long.parseLong(getTextContent(response, "id"));
    producto.nombre = getTextContent(response, "nombre");
    producto.precio = new BigDecimal(getTextContent(response, "precio"));

    return producto;
  }

  /**
   * Obtiene el contenido de texto de un elemento hijo
   */
  private String getTextContent(Element parent, String tagName) {
    NodeList nodes = parent.getElementsByTagNameNS(NAMESPACE, tagName);
    if (nodes.getLength() > 0) {
      return nodes.item(0).getTextContent();
    }
    // Intentar sin namespace
    nodes = parent.getElementsByTagName(tagName);
    if (nodes.getLength() > 0) {
      return nodes.item(0).getTextContent();
    }
    return "";
  }

  /**
   * Método auxiliar para debug - imprime el XML formateado
   */
  public static String prettyPrintXML(String xml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      Document doc = factory
        .newDocumentBuilder()
        .parse(new InputSource(new StringReader(xml)));

      TransformerFactory tf = TransformerFactory.newInstance();
      var transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
        "{http://xml.apache.org/xslt}indent-amount",
        "2"
      );

      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      return writer.toString();
    } catch (Exception e) {
      return xml;
    }
  }

  // ========== DTO ==========

  public static class ProductoSOAP {

    public Long id;
    public String nombre;
    public BigDecimal precio;

    @Override
    public String toString() {
      return String.format(
        "ProductoSOAP[id=%d, nombre=%s, precio=%s]",
        id,
        nombre,
        precio
      );
    }

    public Long getId() {
      return id;
    }

    public String getNombre() {
      return nombre;
    }

    public BigDecimal getPrecio() {
      return precio;
    }
  }
}
