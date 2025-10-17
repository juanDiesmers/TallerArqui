# Sales App (WildFly + MySQL + Email + WebSocket)

## Requisitos
- Java 17
- Maven 3.9+
- WildFly 30+
- Docker (opcional para MySQL/Mailhog)

## Pasos rápidos
1. Levanta infraestructura (opcional pero recomendado para dev):
   ```bash
   docker compose up -d
   # MySQL en localhost:3306 (db=sales user=sales pass=sales)
   # Mailhog UI en http://localhost:8025 (SMTP en 1025)
   ```

2. Construye el WAR:
   ```bash
   mvn clean package
   ```

3. Configura WildFly (desde la carpeta del server):
   - Copia el `mysql-connector-j.jar` a `/opt/wildfly/modules/com/mysql/main/mysql-connector-j.jar` dentro del contenedor o instalación.
   - Conéctate al CLI:
     ```bash
     ${WILDFLY_HOME}/bin/jboss-cli.sh --connect
     ```
   - Ejecuta el script:
     ```bash
     run-batch --file=/path/to/wildfly/setup.cli
     ```
   > Ajusta host/puerto SMTP si no usas Mailhog (por ej. tu proveedor real).

4. Despliega:
   - Copia `target/sales-app.war` a `${WILDFLY_HOME}/standalone/deployments/` o usa `jboss-cli.sh`:
     ```bash
     deploy target/sales-app.war --force
     ```

## Endpoints
- `POST /sales-app/api/orders` crea pedido
- `POST /sales-app/api/orders/{id}/pay` marca pagado
- WebSocket: `ws://<host>:<port>/sales-app/ws/notifications`

## Notas
- Cambia destinatarios en `NotificationListener` por emails reales (cliente/proveedor).
- Seguridad con `@RolesAllowed` requiere configurar realm/JWT en WildFly si aplicas auth.
