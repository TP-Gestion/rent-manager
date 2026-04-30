# Alquileres

Proyecto de gestión de propiedades en alquiler, desarrollado en Java con Spring Boot y PostgreSQL.

## 🚀 Puesta en marcha

### 1. Clonar el repositorio
```bash
git clone <url-del-repo>
```

### 2. Configurar base de datos (Docker)
Se utiliza PostgreSQL en un contenedor Docker:
```bash
docker-compose up -d
```
Variables de entorno configurables en `.env`.

### 3. Levantar la aplicación
```bash
./gradlew bootRun
```

### 4. Linter y formato de código
Para aplicar correcciones automáticas:
```bash
./gradlew spotlessApply
```

## 💻 Uso en IntelliJ IDEA
- Se recomienda ejecutar con una configuración Gradle (`bootRun`).
- Si no inicia, importar el proyecto como Gradle.

## 📁 Estructura principal
- `backend/` - Código fuente y configuración de la app Spring Boot
- `docker-compose.yml` - Orquestación de base de datos PostgreSQL
- `.env` - Variables de entorno

## ⚙️ Variables de entorno principales
Configura el archivo `.env` con los siguientes valores:
```
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
SERVER_PORT
SERVER_HOST
```

## � Documentación

API documentada automáticamente con OpenAPI/Swagger disponible en:
```
http://localhost:8080/swagger-ui.html
```

## �🛠️ Dependencias principales
- Spring Boot (Web, Data JPA, Validation)
- PostgreSQL JDBC
- Hibernate

---
Contribuciones y sugerencias son bienvenidas.