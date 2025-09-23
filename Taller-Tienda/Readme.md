# Proyecto Taller Tienda

Este es un proyecto que implementa una tienda en línea utilizando Spring Boot para el backend, React + Vite para el frontend y MySQL como base de datos, todo gestionado a través de Docker.

## Características

- Backend: Spring Boot con API REST
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
cd taller-tienda

### 2. Levantar los Servicios con Docker Compose

# Construir y levantar todos los servicios en segundo plano
docker-compose up -d --build

### 3. Verificar el Estado de los Contenedores

# Verificar que todos los contenedores estén corriendo
docker ps

Deberías ver una salida similar a:

CONTAINER ID   IMAGE                        COMMAND                  CREATED          STATUS          PORTS                               NAMES
5f45bb84c0e4   taller-tienda-mysql-1        "docker-entrypoint.s…"   12 seconds ago   Up 10 seconds   0.0.0.0:3306->3306/tcp             taller-tienda-mysql-1
a4c57b1b2b1e   taller-tienda-app-1          "/entrypoint.sh"         5 seconds ago    Up 3 seconds    0.0.0.0:8080->8080/tcp             taller-tienda-app-1
c3b82a1d4f5a   taller-tienda-light-1        "docker-entrypoint.s…"   3 seconds ago    Up 2 seconds    0.0.0.0:5173->5173/tcp             taller-tienda-light-1

## Acceso a la Aplicación

- Frontend (React + Vite): http://localhost:5173
- Backend (Spring Boot API): http://localhost:8080
- Base de Datos (MySQL): localhost:3306

## Gestión de Servicios

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

## Estructura del Proyecto

taller-tienda/
├── docker-compose.yml
├── Dockerfile
├── backend/
│   ├── src/
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
└── README.md

## Archivo Docker Compose (docker-compose.yml)

version: '3'

services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: MiClaveSegura123!
      MYSQL_DATABASE: tallerAR
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - backend

  app:
    image: taller-tienda-app
    build:
      context: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - backend

  light:
    image: node:20
    build:
      context: ./frontend
    ports:
      - "5173:5173"
    networks:
      - backend

volumes:
  mysql_data:

networks:
  backend:
    driver: bridge

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

### Reconstruir Imágenes

docker-compose down
docker-compose build --no-cache
docker-compose up -d

### Limpiar Docker

# Eliminar contenedores detenidos
docker container prune

# Eliminar imágenes no utilizadas
docker image prune

# Eliminar volúmenes no utilizados
docker volume prune

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


## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo LICENSE para más detalles.

---

¡Listo para usar! Ejecuta docker-compose up -d --build y tu aplicación estará corriendo en minutos.