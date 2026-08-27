# Arcademania Backend (`arcade-nostalgia`)

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Packaging](https://img.shields.io/badge/Packaging-WAR-lightgrey.svg)

**Arcademania Backend** es un microservicio REST construido con **Java 25** y **Spring Boot 4.1.0** para el catálogo y conservación de máquinas de pinball vintage. Expone endpoints CRUD documentados con OpenAPI/Swagger, persiste los datos en **PostgreSQL** contenedorizado con Docker y centraliza el manejo de errores de negocio a través de un `@RestControllerAdvice`.

---

## Estructura del proyecto

```text
arcademania-backend/
├── pom.xml
├── docker-compose.yml
├── docker/initdb/001-create-schema.sql
├── .env.example
└── src/main/
    ├── java/cl/bahatech/pinball/
    │   ├── PinballApplication.java
    │   ├── ServletInitializer.java
    │   ├── config/OpenApiConfig.java
    │   ├── controller/PinballMachineController.java
    │   ├── domain/PinballMachine.java
    │   ├── dto/
    │   │   ├── PinballMachineRequestDto.java
    │   │   ├── PinballMachineResponseDto.java
    │   │   └── ErrorResponse.java
    │   ├── exception/
    │   │   ├── DuplicatePinballMachineException.java
    │   │   ├── NonExistingPinballMachineException.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── repository/PinballMachineRepository.java
    │   └── service/
    │       ├── PinballMachineService.java
    │       └── PinballMachineServiceImpl.java
    └── resources/
        ├── application.yml
        ├── application-dev.yml
        └── application-prod.yml
```

---

## Variables de entorno

Copia `.env.example` a `.env` para levantar la base de datos local:

```bash
cp .env.example .env
```

| Variable | Descripción | Default local |
| :--- | :--- | :--- |
| `SERVER_PORT` | Puerto donde escucha Spring Boot | `7780` |
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `arcademania_db` |
| `DB_USER` | Usuario de PostgreSQL | `dev_user` |
| `DB_PASSWORD` | Password de PostgreSQL | `change_me` |

> La entidad `PinballMachine` mapea a la tabla `pinball_machines` dentro del schema `pinball`. El script `docker/initdb/001-create-schema.sql` crea ese schema automáticamente la primera vez que se levanta el contenedor.

---

## Levantar todo el stack con Docker (base de datos + aplicación)

```bash
docker compose up -d --build
```

Esto levanta dos contenedores:

- `arcademania-postgres-db`: PostgreSQL 16 con volumen persistente y el schema `pinball` ya creado.
- `arcademania-app`: la aplicación compilada y ejecutada como **WAR ejecutable** (ver sección siguiente), esperando a que la base de datos esté realmente lista (`healthcheck` con `pg_isready`) antes de arrancar.

Para levantar solo la base de datos (por ejemplo, si vas a correr la app desde tu IDE):

```bash
docker compose up -d db
```

Para detener todo:

```bash
docker compose down
```

### ¿Cómo corre un WAR dentro del contenedor?

El proyecto está empaquetado como `.war` (necesario para poder desplegarlo también en un Tomcat externo), con `spring-boot-starter-tomcat` en scope `provided`. Esto no impide correrlo en Docker: el `spring-boot-maven-plugin` genera un **WAR ejecutable híbrido**. Las clases de Tomcat marcadas como `provided` se empaquetan en `WEB-INF/lib-provided`, y el `WarLauncher` de Spring Boot **sí carga ese directorio al classpath** cuando el archivo se ejecuta con `java -jar`. En la práctica:

- `java -jar app.war` → usa el Tomcat embebido incluido en `lib-provided` (esto es lo que hace el `Dockerfile`).
- Copiar el mismo `.war` a `webapps/` de un Tomcat externo → Tomcat ignora `lib-provided` y usa su propio motor de servlets.

Es decir, **el mismo artefacto sirve para ambos destinos**, sin recompilar ni cambiar el `pom.xml`.

El `Dockerfile` incluido hace un build multi-stage:

1. Compila el WAR con Maven Wrapper sobre `eclipse-temurin:25-jdk`.
2. Copia únicamente el `.war` resultante a una imagen liviana `eclipse-temurin:25-jre`.
3. Lo ejecuta con `ENTRYPOINT ["java", "-jar", "app.war"]`.

---

## Ejecutar la aplicación en modo desarrollo (sin Docker para la app)

```bash
docker compose up -d db
./mvnw spring-boot:run
```

Por defecto se activa el perfil `dev` (`application-dev.yml`), que apunta a la base de datos local levantada con Docker y habilita Swagger.

Para producción, se debe activar el perfil `prod`, que exige las variables de entorno de conexión sin valores por defecto y **desactiva Swagger** (`springdoc.api-docs.enabled: false` y `springdoc.swagger-ui.enabled: false`):

```bash
java -Dspring.profiles.active=prod -jar target/arcade-nostalgia-0.0.1-SNAPSHOT.war
```

> Dentro de `docker-compose.yml`, el servicio `app` se conecta a la base de datos usando `DB_HOST=db` y `DB_PORT=5432` (el nombre del servicio y el puerto *interno* del contenedor de Postgres, resueltos por la red de Docker). Esto es independiente del puerto que publiques hacia tu máquina con `DB_PORT` en `.env` — ese solo aplica si te conectas a la base de datos desde fuera de la red de Docker (por ejemplo, desde tu IDE o un cliente SQL).

---

## Documentación y pruebas de contrato

- **Swagger UI**: `http://localhost:7780/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:7780/api-docs`

Los controladores y DTOs están anotados con `@Tag`, `@Operation`, `@ApiResponses` y `@Schema` (en español), por lo que la consola de Swagger permite documentar y ejecutar cada endpoint con el botón "Try it out".

---

## Endpoints (`/api/v1/pinballs`)

| Método | Endpoint | Descripción | Status Code |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/pinballs` | Lista todas las máquinas registradas | `200 OK` / `204 No Content` |
| `GET` | `/api/v1/pinballs/{id}` | Obtiene una máquina por ID | `200 OK` / `404 Not Found` |
| `POST` | `/api/v1/pinballs` | Registra una nueva máquina | `201 Created` / `400 Bad Request` / `409 Conflict` |
| `PUT` | `/api/v1/pinballs/{id}` | Actualiza una máquina existente | `200 OK` / `400 Bad Request` / `404 Not Found` |
| `DELETE` | `/api/v1/pinballs/{id}` | Elimina una máquina | `204 No Content` / `404 Not Found` |

### Ejemplo de creación

```json
{
  "modelName": "Medieval Madness",
  "manufacturer": "Williams",
  "rarityTier": "Edicion Limitada",
  "imageUrl": "https://example.com/medieval-madness.jpg",
  "historicalSummary": "Considerado uno de los mejores pinballs de los anos 90",
  "releaseYear": 1997,
  "unitsProduced": 4016,
  "restorationCostUsd": 1200.50,
  "conditionRating": 4.5,
  "isFullyFunctional": true,
  "hasMultiball": true
}
```

### Manejo de errores centralizado

| Escenario | HTTP Status | Ejemplo de payload |
| :--- | :--- | :--- |
| Máquina no encontrada | `404 Not Found` | `{"code": 404, "message": "Pinball machine with ID 99 not found", "timestamp": "..."}` |
| Nombre de modelo duplicado | `409 Conflict` | `{"code": 409, "message": "A pinball machine with model name 'Medieval Madness' already exists", "timestamp": "..."}` |
| Error de validación de DTO | `400 Bad Request` | `{"code": 400, "message": "releaseYear: Release year is required", "timestamp": "..."}` |
| Error inesperado del servidor | `500 Internal Server Error` | `{"code": 500, "message": "Unexpected server error", "timestamp": "..."}` |

Todos los errores son interceptados por `GlobalExceptionHandler` (`@RestControllerAdvice`), por lo que ningún endpoint devuelve un stacktrace nativo del servidor.

---

## cURL de ejemplo

```bash
# Listar maquinas
curl -i -X GET http://localhost:7780/api/v1/pinballs

# Crear una maquina
curl -i -X POST http://localhost:7780/api/v1/pinballs \
  -H "Content-Type: application/json" \
  -d '{
    "modelName": "Medieval Madness",
    "manufacturer": "Williams",
    "rarityTier": "Edicion Limitada",
    "releaseYear": 1997,
    "unitsProduced": 4016,
    "isFullyFunctional": true,
    "hasMultiball": true
  }'

# Obtener maquina por ID
curl -i -X GET http://localhost:7780/api/v1/pinballs/1

# Actualizar maquina
curl -i -X PUT http://localhost:7780/api/v1/pinballs/1 \
  -H "Content-Type: application/json" \
  -d '{
    "modelName": "Medieval Madness",
    "manufacturer": "Williams",
    "rarityTier": "Leyenda",
    "releaseYear": 1997,
    "unitsProduced": 4016,
    "isFullyFunctional": true,
    "hasMultiball": true
  }'

# Eliminar maquina
curl -i -X DELETE http://localhost:7780/api/v1/pinballs/1
```
