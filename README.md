# Arcademania Backend (`arcade-nostalgia`)

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Packaging](https://img.shields.io/badge/Packaging-WAR-lightgrey.svg)
![Tests](https://img.shields.io/badge/Tests-JUnit%205%20%2B%20Mockito-25A162.svg)
![Coverage](https://img.shields.io/badge/Coverage-JaCoCo-yellow.svg)

**Arcademania Backend** es un microservicio REST desarrollado con **Java 25** y **Spring Boot 4.1.0**, orientado al catálogo y conservación de máquinas de pinball vintage. Expone endpoints CRUD y de búsqueda documentados mediante OpenAPI/Swagger, persiste la información en **PostgreSQL** contenedorizado con Docker, valida sus propias invariantes de dominio, centraliza el manejo de errores a través de un `@RestControllerAdvice`, y cuenta con una suite de pruebas unitarias con cobertura medida por JaCoCo.

---

## Arquitectura del proyecto

El proyecto sigue una separación por capas inspirada en arquitectura hexagonal: el paquete `domain` contiene el modelo de negocio puro (sin ninguna dependencia de framework ni de persistencia), `application` contiene la lógica de aplicación (patrón Service/ServiceImpl), e `infrastructure` contiene los adaptadores de entrada (controladores REST) y de salida (persistencia JPA).

```text
arcademania-backend/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── docker/initdb/001-create-schema.sql
├── .env.example
└── src/
    ├── main/
    │   ├── java/cl/bahatech/pinball/
    │   │   ├── PinballApplication.java
    │   │   ├── ServletInitializer.java
    │   │   ├── application/service/
    │   │   │   ├── PinballMachineService.java
    │   │   │   └── PinballMachineServiceImpl.java
    │   │   ├── domain/
    │   │   │   ├── model/PinballMachine.java
    │   │   │   └── exception/
    │   │   │       ├── DuplicatePinballMachineException.java
    │   │   │       ├── NonExistingPinballMachineException.java
    │   │   │       ├── InvalidPinballMachineModelNameException.java
    │   │   │       ├── InvalidPinballMachineManufacturerException.java
    │   │   │       ├── InvalidPinballMachineReleaseYearException.java
    │   │   │       ├── InvalidPinballMachineUnitsProducedException.java
    │   │   │       ├── InvalidPinballMachineRestorationCostException.java
    │   │   │       ├── InvalidPinballMachineConditionRatingException.java
    │   │   │       └── InvalidPinballMachineFunctionalStatusException.java
    │   │   ├── infrastructure/
    │   │   │   ├── persistence/PinballMachineEntity.java
    │   │   │   └── web/
    │   │   │       ├── config/
    │   │   │       │   ├── OpenApiConfig.java
    │   │   │       │   └── CorsConfig.java
    │   │   │       ├── controller/PinballMachineController.java
    │   │   │       ├── dto/
    │   │   │       │   ├── PinballMachineRequestDto.java
    │   │   │       │   ├── PinballMachineResponseDto.java
    │   │   │       │   └── ErrorResponse.java
    │   │   │       └── exception/GlobalExceptionHandler.java
    │   │   └── repository/PinballMachineRepository.java
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       └── data.sql
    └── test/java/cl/bahatech/pinball/
        ├── domain/model/TestPinballMachine.java
        ├── application/service/TestPinballMachineServiceImpl.java
        └── infrastructure/
            ├── persistence/TestPinballMachineEntity.java
            └── web/
                ├── controller/TestPinballMachineController.java
                ├── exception/TestGlobalExceptionHandler.java
                └── config/
                    ├── TestCorsConfig.java
                    └── TestOpenApiConfig.java
```

`PinballMachine` (en `domain.model`) es una clase de dominio que valida sus propias invariantes en el constructor y en cada setter (nombre de modelo, fabricante, año de lanzamiento, unidades producidas, costo de restauración, rating de condición e indicadores de funcionalidad), lanzando una excepción de dominio específica ante cualquier valor inválido. `PinballMachineEntity` (en `infrastructure.persistence`) es la entidad JPA correspondiente, sin ninguna regla de negocio. La conversión entre ambas se realiza en `PinballMachineServiceImpl`; la conversión entre `PinballMachine` y los DTO de la API se realiza en `PinballMachineController`.

---

## Variables de entorno

Copiar `.env.example` a `.env` antes de levantar el proyecto:

```bash
cp .env.example .env
```

| Variable | Descripción | Valor por defecto (local) |
| :--- | :--- | :--- |
| `SERVER_PORT` | Puerto en el que escucha la aplicación | `7780` |
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `arcademania_db` |
| `DB_USER` | Usuario de PostgreSQL | `dev_user` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `change_me` |
| `FRONTEND_URL` | Origen permitido por CORS (donde corre el frontend) | `http://localhost:5173` |

La entidad `PinballMachineEntity` mapea a la tabla `pinball_machines` dentro del schema `pinball`. Dicho schema se crea automáticamente mediante `docker/initdb/001-create-schema.sql` la primera vez que se inicializa el contenedor de base de datos.

## CORS

El backend habilita CORS únicamente para el origen configurado en `FRONTEND_URL` (por defecto `http://localhost:5173`, el puerto por defecto de Vite), aplicado sobre todas las rutas bajo `/api/**` mediante `CorsConfig` (`infrastructure.web.config`). Si el frontend corre en otro puerto o dominio, debe actualizarse esta variable de entorno.

---

## Levantamiento del stack completo con Docker

```bash
docker compose up -d --build
```

Este comando levanta dos contenedores:

- `arcademania-postgres-db`: PostgreSQL 16 con volumen persistente.
- `arcademania-app`: la aplicación compilada y ejecutada como WAR ejecutable, la cual espera a que la base de datos esté disponible (`pg_isready`) antes de iniciar.

Detener el stack:

```bash
docker compose down
```

Si se modificó código fuente o configuración, reconstruir imagen y reiniciar volúmenes para evitar inconsistencias:

```bash
docker compose down -v --rmi local
docker compose up -d --build
```

### Consideraciones sobre el empaquetado WAR

El proyecto se empaqueta como `.war`, con `spring-boot-starter-tomcat` en scope `provided`, para poder desplegarse tanto en un Tomcat externo como en un contenedor Docker sin recompilar. El `spring-boot-maven-plugin` genera un WAR ejecutable híbrido: las clases de Tomcat quedan en `WEB-INF/lib-provided`, directorio que `WarLauncher` carga al ejecutar `java -jar`, pero que un Tomcat externo ignora (usa su propio motor de servlets).

---

## Datos de prueba precargados

El perfil `dev` precarga automáticamente registros de ejemplo mediante `src/main/resources/data.sql`, habilitado por `spring.jpa.defer-datasource-initialization: true` y `spring.sql.init.mode: always` (garantizan que el script corra después de que Hibernate cree las tablas). Las inserciones usan `ON CONFLICT (model_name) DO NOTHING`, por lo que reiniciar la app no genera duplicados.

---

## Ejecución en modo desarrollo (sin contenedor para la aplicación)

```bash
docker compose up -d db
./mvnw spring-boot:run
```

Por defecto se activa el perfil `dev`, que apunta a la base de datos local y habilita Swagger.

Para `prod`, es obligatorio proveer las variables de conexión (no tiene valores por defecto), y Swagger queda deshabilitado:

```bash
java -Dspring.profiles.active=prod -jar target/arcade-nostalgia-0.0.1-SNAPSHOT.war
```

---

## Documentación y pruebas de contrato

- **Swagger UI**: `http://localhost:7780/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:7780/api-docs`

Controladores y DTO están anotados con `@Tag`, `@Operation`, `@ApiResponses` y `@Schema`, redactados en español.

---

## Endpoints (`/api/v1/pinballs`)

| Método | Endpoint | Descripción | Código de respuesta |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/pinballs` | Lista todas las máquinas registradas | `200 OK` / `204 No Content` |
| `GET` | `/api/v1/pinballs/{id}` | Obtiene una máquina por ID | `200 OK` / `404 Not Found` |
| `GET` | `/api/v1/pinballs/search?modelName=` | Obtiene una máquina por nombre de modelo exacto (sin distinguir mayúsculas/minúsculas) | `200 OK` / `404 Not Found` |
| `POST` | `/api/v1/pinballs` | Registra una nueva máquina | `201 Created` / `400` / `409` / `422` |
| `PUT` | `/api/v1/pinballs/{id}` | Actualiza una máquina existente | `200 OK` / `400` / `404` / `422` |
| `DELETE` | `/api/v1/pinballs/{id}` | Elimina una máquina | `204 No Content` / `404 Not Found` |

### Manejo centralizado de errores

| Escenario | Código HTTP | Ejemplo |
| :--- | :--- | :--- |
| Máquina no encontrada | `404` | `{"code": 404, "message": "Pinball machine with ID 99 not found", "timestamp": "..."}` |
| Nombre de modelo duplicado | `409` | `{"code": 409, "message": "A pinball machine with model name 'Haunted Madness' already exists", "timestamp": "..."}` |
| Error de validación en el DTO | `400` | `{"code": 400, "message": "releaseYear: Release year is required", "timestamp": "..."}` |
| Violación de invariante de dominio | `422` | `{"code": 422, "message": "Release year must be between 1930 and 2100", "timestamp": "..."}` |
| Error inesperado | `500` | `{"code": 500, "message": "Unexpected server error", "timestamp": "..."}` |

`GlobalExceptionHandler` (`@RestControllerAdvice`) intercepta todos los errores; ningún endpoint retorna un stacktrace nativo del servidor.

### Ejemplo de uso: búsqueda por nombre de modelo

```bash
curl -i "http://localhost:7780/api/v1/pinballs/search?modelName=Twilight%20Zone"
```

```json
{
  "id": 1,
  "modelName": "Twilight Zone",
  "manufacturer": "Bally",
  "rarityTier": "Leyenda",
  "imageUrl": "https://www.bahatech.cl/pinball/galaxy.svg",
  "historicalSummary": "Basada en la mítica serie de televisión...",
  "releaseYear": 1993,
  "unitsProduced": 15235,
  "restorationCostUsd": 1450.00,
  "conditionRating": 4.9,
  "isFullyFunctional": true,
  "hasMultiball": true
}
```

Si no existe, responde `404 Not Found`. La búsqueda no distingue mayúsculas/minúsculas (`findByModelNameIgnoreCase`).

---

## Pruebas unitarias y cobertura de código

Suite con **JUnit 5** y **Mockito**, cubriendo el modelo de dominio (validación exhaustiva de cada invariante), el service, el controller, el manejador global de excepciones y las clases de configuración (`CorsConfig`, `OpenApiConfig`). Convención de nombres `TestNombreDeLaClase` (estilo `baha-rigvault-core`).

```bash
./mvnw clean test
```

Reporte de cobertura con JaCoCo:

```bash
mvn jacoco:report
```

Disponible en `target/site/jacoco/index.html`. Solo `PinballApplication`, `ServletInitializer` y los DTO (`infrastructure.web.dto`) quedan excluidos del cálculo, por ser código de arranque o records de transporte de datos sin ramas ni lógica propia.

---

## Casos de prueba para Swagger

Cuatro objetos JSON listos para `POST /api/v1/pinballs` desde Swagger ("Try it out").

### Caso 1 — Haunted Madness (caso estándar completo)

```json
{
  "modelName": "Haunted Madness",
  "manufacturer": "Spooky Pinball",
  "rarityTier": "De Colección",
  "imageUrl": "https://www.bahatech.cl/pinball/hauntedmadness.svg",
  "releaseYear": 2018,
  "unitsProduced": 1200,
  "restorationCostUsd": 1120.50,
  "conditionRating": 4.2,
  "isFullyFunctional": true,
  "hasMultiball": true
}
```

### Caso 2 — Neon Rush 2077 (rating máximo)

```json
{
  "modelName": "Neon Rush 2077",
  "manufacturer": "Cyber Arcade Co.",
  "rarityTier": "Edición Limitada",
  "imageUrl": "https://www.bahatech.cl/pinball/neonrush.svg",
  "releaseYear": 2026,
  "unitsProduced": 500,
  "restorationCostUsd": 2450,
  "conditionRating": 5.0,
  "isFullyFunctional": true,
  "hasMultiball": true
}
```

### Caso 3 — Radical Flippers (sin modo multiball)

```json
{
  "modelName": "Radical Flippers",
  "manufacturer": "Bally / Midway",
  "rarityTier": "Clásica",
  "imageUrl": "https://www.bahatech.cl/pinball/radicalflippers.svg",
  "releaseYear": 1988,
  "unitsProduced": 6500,
  "restorationCostUsd": 980,
  "conditionRating": 3.9,
  "isFullyFunctional": true,
  "hasMultiball": false
}
```

### Caso 4 — Space Cadet (solo campos obligatorios)

```json
{
  "modelName": "Space Cadet",
  "manufacturer": "Maxis",
  "releaseYear": 1995,
  "isFullyFunctional": false,
  "hasMultiball": false
}
```

> Ninguno de los cuatro colisiona con los registros precargados (`Twilight Zone`, `The Addams Family`), por lo que no deberían retornar `409 Conflict`.