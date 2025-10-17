# Proyecto Taller Tienda

Este es un proyecto que implementa una tienda en línea utilizando Spring Boot para el backend, React + Vite para el frontend y MySQL como base de datos, todo gestionado a través de Docker.

## Características

- Backend: Spring Boot con API REST, ademas se realizaron pruebas de transacciones con JTA y Notificaciones manejando Kafka
- Frontend: React + Vite
- Base de Datos: MySQL 8
- Contenedorización: Docker y Docker Compose
- Persistencia: Volúmenes Docker para datos de MySQL

## Requisitos Previos

1. Docker: Versión 20.10 o superior
2. Docker Compose: Versión 2.0 o superior
3. WSL 2 (en Windows): Recomendado para mejor rendimiento
4. Git: Para clonar el repositorio

## Instalación y Configuración

### 1. Clonar el Repositorio

git clone https://github.com/juanDiesmers/TallerArqui.git
cd Taller-Tienda

### 2. Levantar los Servicios con Docker Compose

# Construir y levantar todos los servicios en segundo plano
docker-compose up -d --build o docker-compose up --build (si se requieren ver los logs dentro de la terminal)

### 3. Verificar el Estado de los Contenedores

# Verificar que todos los contenedores estén corriendo
docker ps

## Acceso a la Aplicación

- Frontend (React + Vite): http://localhost:5173
- Base de Datos (MySQL): localhost:3306

## Gestión de Servicios

### Probar las trasancciones con JTA

si se quiere probar el exito de una transaccion (todo el proceso se realiza correctamente) 
- acceder a http://localhost:8080/api/bill?productoId=1&cantidad=2&clienteId=1&metodoPagoId=1&simulateFailure=false

si se quiere probar el fracaso de una transaccion (se procede a dañar el ultimo paso, toda la transaccion se cancela y se hace un rollback, en base de datos se puede observar que la informacion que se alcanza a guardar antes de termianr la transaccion se elimina y no queda rastro de la misma, manteniendo los datos que ya se tenian)
- acceder a http://localhost:8080/api/bill?productoId=1&cantidad=2&clienteId=1&metodoPagoId=1&simulateFailure=true

### Probar la notificaciones con  kafka y MD que se consume como test 

- acceder a http://localhost:8080/api/notificaciones/test

### Ver Logs en Tiempo Real

# Backend (Spring Boot)
docker-compose logs -f app

# Frontend (React + Vite)
docker-compose logs -f light

# MySQL
docker-compose logs -f mysql

### Detener los Servicios

# Detener y eliminar contenedores
docker-compose down

# Detener sin eliminar contenedores
docker-compose stop

### Reiniciar Servicios

# Reiniciar todos los servicios
docker-compose restart

# Reiniciar un servicio específico
docker-compose restart app

## Gestión de Base de Datos

### Acceder a MySQL

# Acceder al contenedor de MySQL
docker exec -it taller-tienda-mysql-1 mysql -uroot -p

Contraseña: MiClaveSegura123!

### Comandos Útiles en MySQL

-- Mostrar bases de datos
SHOW DATABASES;

-- Usar la base de datos del proyecto
USE tallerAR;

-- Mostrar tablas
SHOW TABLES;

-- Ver datos de productos (ejemplo)
SELECT * FROM producto LIMIT 5;

## Solución de Problemas

### Puerto Ocupado

# Ver qué proceso está usando el puerto
sudo lsof -i :3306  # Para MySQL
sudo lsof -i :8080  # Para Spring Boot
sudo lsof -i :5173  # Para Vite

# O detener servicios locales que puedan estar conflictuando
sudo service mysql stop  # En Linux

### Problemas de Conexión entre Contenedores

docker network ls
docker inspect taller-tienda_backend

### Reconstruir Imágenes y limpiar docker

docker-compose down --rmi all
docker-compose down -v
docker-compose build app
docker-compose up 

## Notas Importantes

- Persistencia: Los datos de MySQL se guardan en un volumen Docker llamado mysql_data
- Variables de Entorno: Las configuraciones sensibles están definidas en el docker-compose.yml
- Redes: Todos los servicios están conectados a la red backend
- Dependencias: El backend espera a que MySQL esté disponible antes de iniciar

## Despliegue en Producción

Para entornos de producción, considera:

1. Cambiar las contraseñas por defecto
2. Configurar SSL/TLS
3. Implementar un reverse proxy (Nginx)
4. Configurar backups automáticos de la base de datos
5. Implementar monitoreo y logs centralizados
