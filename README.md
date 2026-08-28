# Arcademania Backend (`arcade-nostalgia`)

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Packaging](https://img.shields.io/badge/Packaging-WAR-lightgrey.svg)

**Arcademania Backend** es un microservicio REST desarrollado con **Java 25** y **Spring Boot 4.1.0**, orientado al catálogo y conservación de máquinas de pinball vintage. Expone endpoints CRUD documentados mediante OpenAPI/Swagger, persiste la información en **PostgreSQL** contenedorizado con Docker, y centraliza el manejo de errores de negocio a través de un `@RestControllerAdvice`.

---

## Arquitectura del proyecto

El proyecto sigue una separación por capas inspirada en arquitectura hexagonal: el paquete `domain` contiene el modelo de negocio puro (sin dependencias de framework ni de persistencia), `application` contiene los casos de uso, e `infrastructure` contiene los adaptadores de entrada (controladores REST) y de salida (persistencia JPA).

```text
arcademania-backend/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── docker/initdb/001-create-schema.sql
├── .env.example
└── src/main/
    ├── java/cl/bahatech/pinball/
    │   ├── PinballApplication.java
    │   ├── ServletInitializer.java
    │   ├── application/service/
    │   │   ├── PinballMachineService.java
    │   │   └── PinballMachineServiceImpl.java
    │   ├── domain/
    │   │   ├── model/PinballMachine.java
    │   │   └── exception/
    │   │       ├── DuplicatePinballMachineException.java
    │   │       └── NonExistingPinballMachineException.java
    │   ├── infrastructure/
    │   │   ├── persistence/PinballMachineEntity.java
    │   │   └── web/
    │   │       ├── config/OpenApiConfig.java
    │   │       ├── controller/PinballMachineController.java
    │   │       ├── dto/
    │   │       │   ├── PinballMachineRequestDto.java
    │   │       │   ├── PinballMachineResponseDto.java
    │   │       │   └── ErrorResponse.java
    │   │       └── exception/GlobalExceptionHandler.java
    │   └── repository/PinballMachineRepository.java
    └── resources/
        ├── application.yml
        ├── application-dev.yml
        ├── application-prod.yml
        └── data.sql
```

`PinballMachine` (en `domain.model`) es un `record` inmutable, libre de anotaciones de persistencia. `PinballMachineEntity` (en `infrastructure.persistence`) es la entidad JPA correspondiente. La conversión entre ambas se realiza en `PinballMachineServiceImpl`, mientras que la conversión entre `PinballMachine` y los DTO de la API se realiza en `PinballMachineController`.

---

## Variables de entorno

Se debe copiar `.env.example` a `.env` antes de levantar el proyecto:

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

La entidad `PinballMachineEntity` mapea a la tabla `pinball_machines` dentro del schema `pinball`. Dicho schema es creado automáticamente mediante el script `docker/initdb/001-create-schema.sql` la primera vez que se inicializa el contenedor de base de datos.

---

## Levantamiento del stack completo con Docker

Se recomienda ejecutar el siguiente comando desde la raíz del proyecto:

```bash
docker compose up -d --build
```

Este comando levanta dos contenedores:

- `arcademania-postgres-db`: instancia de PostgreSQL 16 con volumen persistente.
- `arcademania-app`: aplicación compilada y ejecutada como WAR ejecutable, la cual espera a que la base de datos se encuentre disponible (verificación mediante `pg_isready`) antes de iniciar.

Para detener el stack:

```bash
docker compose down
```

En caso de haberse modificado el código fuente o la configuración, se recomienda reconstruir la imagen y reiniciar los volúmenes para evitar inconsistencias:

```bash
docker compose down -v --rmi local
docker compose up -d --build
```

### Consideraciones sobre el empaquetado WAR

El proyecto se empaqueta como `.war`, con la dependencia `spring-boot-starter-tomcat` declarada en scope `provided`, con el propósito de permitir su despliegue tanto en un servidor Tomcat externo como en un contenedor Docker sin necesidad de recompilar. El `spring-boot-maven-plugin` genera un WAR ejecutable híbrido: las clases de Tomcat se almacenan en `WEB-INF/lib-provided`, directorio que es cargado por `WarLauncher` al ejecutar `java -jar`, pero ignorado por un servidor Tomcat externo, el cual emplea su propio motor de servlets.

---

## Datos de prueba precargados

El perfil `dev` precarga automáticamente registros de ejemplo mediante `src/main/resources/data.sql`. Dicho comportamiento se habilita a través de las propiedades `spring.jpa.defer-datasource-initialization: true` y `spring.sql.init.mode: always`, las cuales garantizan que el script se ejecute después de que Hibernate haya creado las tablas correspondientes. Las inserciones utilizan la cláusula `ON CONFLICT (model_name) DO NOTHING`, por lo que sucesivos reinicios de la aplicación no generan registros duplicados.

---

## Ejecución en modo desarrollo (sin contenedor para la aplicación)

```bash
docker compose up -d db
./mvnw spring-boot:run
```

Por defecto se activa el perfil `dev` (`application-dev.yml`), el cual apunta a la base de datos local levantada mediante Docker y habilita la consola de Swagger.

Para el perfil `prod`, es obligatorio proveer las variables de entorno de conexión, dado que dicho perfil no define valores por defecto, y la consola de Swagger permanece deshabilitada (`springdoc.api-docs.enabled: false` y `springdoc.swagger-ui.enabled: false`):

```bash
java -Dspring.profiles.active=prod -jar target/arcade-nostalgia-0.0.1-SNAPSHOT.war
```

---

## Documentación y pruebas de contrato

- **Swagger UI**: `http://localhost:7780/swagger-ui.html`
- **Especificación OpenAPI (JSON)**: `http://localhost:7780/api-docs`

Los controladores y los DTO se encuentran anotados con `@Tag`, `@Operation`, `@ApiResponses` y `@Schema`, redactados en idioma español, lo cual permite documentar y ejecutar cada endpoint mediante la funcionalidad "Try it out" de la consola de Swagger.

---

## Endpoints (`/api/v1/pinballs`)

| Método | Endpoint | Descripción | Código de respuesta |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/pinballs` | Obtiene la totalidad de las máquinas registradas | `200 OK` / `204 No Content` |
| `GET` | `/api/v1/pinballs/{id}` | Obtiene una máquina a partir de su identificador | `200 OK` / `404 Not Found` |
| `POST` | `/api/v1/pinballs` | Registra una nueva máquina | `201 Created` / `400 Bad Request` / `409 Conflict` |
| `PUT` | `/api/v1/pinballs/{id}` | Actualiza una máquina existente | `200 OK` / `400 Bad Request` / `404 Not Found` |
| `DELETE` | `/api/v1/pinballs/{id}` | Elimina una máquina existente | `204 No Content` / `404 Not Found` |

### Manejo centralizado de errores

| Escenario | Código HTTP | Ejemplo de respuesta |
| :--- | :--- | :--- |
| Máquina no encontrada | `404 Not Found` | `{"code": 404, "message": "Pinball machine with ID 99 not found", "timestamp": "..."}` |
| Nombre de modelo duplicado | `409 Conflict` | `{"code": 409, "message": "A pinball machine with model name 'Haunted Madness' already exists", "timestamp": "..."}` |
| Error de validación en el DTO | `400 Bad Request` | `{"code": 400, "message": "releaseYear: Release year is required", "timestamp": "..."}` |
| Error inesperado del servidor | `500 Internal Server Error` | `{"code": 500, "message": "Unexpected server error", "timestamp": "..."}` |

La totalidad de los errores es interceptada por `GlobalExceptionHandler` (`@RestControllerAdvice`), por lo cual ningún endpoint retorna un stacktrace nativo del servidor.

---

## Casos de prueba para Swagger

A continuación se presentan cuatro objetos en formato JSON, listos para ser utilizados en el cuerpo de la solicitud `POST /api/v1/pinballs` desde la consola de Swagger ("Try it out").

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

### Caso 2 — Neon Rush 2077 (rating máximo, sin uso)

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

### Caso 3 — Radical Flippers (máquina sin modo multiball)

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

### Caso 4 — Space Cadet (prueba sin campos opcionales)

Caso destinado a validar que la creación de una máquina es exitosa proveyendo únicamente los campos obligatorios (`modelName`, `manufacturer`, `releaseYear`, `isFullyFunctional`, `hasMultiball`), sin incluir `rarityTier`, `imageUrl`, `historicalSummary`, `unitsProduced`, `restorationCostUsd` ni `conditionRating`.

```json
{
  "modelName": "Space Cadet",
  "manufacturer": "Maxis",
  "releaseYear": 1995,
  "isFullyFunctional": false,
  "hasMultiball": false
}
```

> Ninguno de los cuatro casos colisiona con los registros precargados por `data.sql` (`Twilight Zone` y `The Addams Family`), por lo que su creación no debiese retornar `409 Conflict`.
