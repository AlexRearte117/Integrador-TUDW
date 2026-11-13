# TP Integrador · DAW1 — Taller Mecánico
Alumno/a: Rearte Fonzalida Alex

Legajo: 510145

Enunciado elegido: Taller
# Descripción

API para seguir cada auto desde que ingresa al taller hasta que se entrega al cliente.
Incluye un panel web estático para operar la API desde el navegador.

## Tecnologías

- Java 21
- Spring Boot 4.1.2
- Spring Data JPA / Hibernate
- H2 (base de datos en memoria)
- Maven
- HTML + CSS + JavaScript (vanilla, sin frameworks)

## Ejecutar

Requisito: **JDK 21**. El proyecto compila con `java.version=21`, así que un JDK 17 o inferior falla con
`error: release version 21 not supported`.

### Con Maven Wrapper

```powershell
# PowerShell (Windows): apuntar JAVA_HOME a un JDK 21
$env:JAVA_HOME = "C:\ruta\a\jdk-21"
.\mvnw.cmd spring-boot:run
```

```bash
# Linux / macOS
./mvnw spring-boot:run
```

### Con el jar empaquetado

```powershell
$env:JAVA_HOME = "C:\ruta\a\jdk-21"
.\mvnw.cmd clean package -DskipTests
& "$env:JAVA_HOME\bin\java.exe" -jar target\taller-0.0.1-SNAPSHOT.jar
```

Una vez iniciado:

- Panel web: `http://localhost:8080/`
- API REST: `http://localhost:8080/api/...`
- Consola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taller`, usuario `sa`, sin contraseña).

> La base H2 es **en memoria**: los datos se pierden al reiniciar la aplicación.

## Panel web

El panel está en `src/main/resources/static/` y Spring Boot lo sirve automáticamente en la raíz del sitio:

| Archivo | Rol |
|---|---|
| `index.html` | Estructura del panel: pestañas de Clientes, Vehículos, Repuestos, Órdenes y Facturación |
| `styles.css` | Estilos de la interfaz |
| `app.js` | Lógica del panel: llama a la API con `fetch()` y renderiza los resultados |

### Cómo funciona

- Spring Boot sirve los archivos de `static/` en `http://localhost:8080/`; `index.html` es la página de bienvenida.
- `app.js` consume los endpoints REST de `/api` con `fetch()`. Al servirse el HTML y la API desde el
  **mismo origen**, no hace falta configurar CORS.
- El panel es solo **presentación**: no tiene reglas de negocio. Arma el JSON de cada petición, muestra los
  datos y presenta el mensaje de error que devuelve la API (por ejemplo, un `409` por stock insuficiente).
- Toda la lógica y las validaciones siguen en el backend, por lo que la misma API puede usarse desde
  Postman, otro frontend o una app móvil sin cambios.

### Funcionalidades

- **Clientes:** alta, edición, baja y listado de los vehículos de un cliente.
- **Vehículos:** alta, edición, baja e historial de órdenes por patente.
- **Repuestos:** alta, edición, baja y control de stock.
- **Órdenes:** creación, filtro por estado, cambio de estado, carga de costo final y agregado de repuestos
  (descuenta stock).
- **Facturación:** consulta de órdenes entregadas y total facturado por mes.

## Guía de demo

Flujo sugerido para mostrar el panel:

1. **Clientes** → crear “Juan Pérez / 123456789”.
2. **Vehículos** → patente `ABC123`, marca/modelo, elegir el cliente → crear.
3. **Repuestos** → “Filtro de aceite”, precio 1000, stock 5.
4. **Órdenes** → elegir vehículo + descripción → crear (nace `INGRESADO`).
5. En la tabla de órdenes, tocar **Abrir** y mostrar el diálogo:
   - Agregar repuesto (cantidad 2) → baja el stock y aparece `costoRepuestos = 2000`.
   - Cargar costo final `25000`.
   - Cambiar estado: `EN_REPARACION` → `TERMINADO` (mirar `horasEnReparacion`) → `ENTREGADO`.
6. Probar un error: intentar pasar a `ENTREGADO` sin costo, o cargar otra orden al mismo vehículo →
   la pantalla muestra el `409` con el mensaje del backend.
7. **Vehículos → Historial por patente** → `ABC123`.
8. **Facturación** → elegir el mes actual y consultar cantidad/total.

## Recursos y endpoints

### Clientes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/clientes/{id}` | Obtiene un cliente |
| POST | `/api/clientes` | Crea un cliente |
| PUT | `/api/clientes/{id}` | Actualiza un cliente |
| DELETE | `/api/clientes/{id}` | Elimina un cliente |
| GET | `/api/clientes/{id}/vehiculos` | Vehículos del cliente (404 si no existe) |

Body de creación/actualización:

```json
{ "nombre": "Juan Pérez", "telefono": "123456789" }
```

### Vehículos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/vehiculos` | Lista vehículos |
| GET | `/api/vehiculos/{id}` | Obtiene un vehículo |
| POST | `/api/vehiculos` | Crea un vehículo |
| PUT | `/api/vehiculos/{id}` | Actualiza un vehículo |
| DELETE | `/api/vehiculos/{id}` | Elimina un vehículo |
| GET | `/api/vehiculos/patente/{patente}/historial` | Órdenes del vehículo, de la más nueva a la más vieja |

Body:

```json
{
  "patente": "ABC123",
  "marca": "Ford",
  "modelo": "Fiesta",
  "clienteId": "UUID_DEL_CLIENTE"
}
```

La patente se normaliza a mayúsculas y debe cumplir `ABC123` o `AB123CD`.

### Órdenes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/ordenes` | Lista órdenes. Filtro opcional: `?estado=EN_REPARACION` |
| GET | `/api/ordenes/{id}` | Obtiene una orden |
| POST | `/api/ordenes` | Crea una orden en estado `INGRESADO` |
| PUT | `/api/ordenes/{id}` | Actualiza descripción, costo final y/o estado |
| PATCH / PUT | `/api/ordenes/{id}/estado` | Cambia solo el estado |
| POST | `/api/ordenes/{id}/repuestos` | Agrega un repuesto y descuenta stock |
| GET | `/api/ordenes/facturacion?mes=2026-11` | Cantidad de órdenes entregadas y total facturado del mes |

Crear orden:

```json
{ "vehiculoId": "UUID_DEL_VEHICULO", "descripcion": "Cambio de aceite" }
```

Actualizar orden (los campos nulos se ignoran):

```json
{ "descripcion": "Cambio de aceite y filtro", "costoFinal": 25000, "estado": "EN_REPARACION" }
```

Agregar repuesto:

```json
{ "repuestoId": "UUID_DEL_REPUESTO", "cantidad": 2 }
```

### Repuestos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/repuestos` | Lista repuestos |
| GET | `/api/repuestos/{id}` | Obtiene un repuesto |
| POST | `/api/repuestos` | Crea un repuesto |
| PUT | `/api/repuestos/{id}` | Actualiza un repuesto |
| DELETE | `/api/repuestos/{id}` | Elimina un repuesto |

Body:

```json
{ "nombre": "Filtro de aceite", "precio": 1000, "stock": 5 }
```

## Estados de la orden

```
INGRESADO -> EN_REPARACION | CANCELADO
EN_REPARACION -> TERMINADO
TERMINADO -> ENTREGADO
```

Cada cambio de estado guarda fecha y hora. Cuando la orden pasa por `EN_REPARACION` y `TERMINADO`, la respuesta incluye `horasEnReparacion`.

## Reglas de negocio

- **409** si un vehículo con una orden abierta (ni entregada ni cancelada) intenta tener otra.
- **409** si se intenta crear/actualizar un vehículo con una patente ya existente.
- **409** si se intenta entregar una orden sin `costoFinal`.
- **409** si no hay stock suficiente para agregar un repuesto.
- **400** si `costoFinal` es menor al `costoRepuestos` de la orden.
- **400** para datos obligatorios faltantes o con formato inválido.
- **404** para recursos inexistentes.

## Respuesta de una orden

```json
{
  "id": "UUID",
  "vehiculoId": "UUID",
  "patente": "ABC123",
  "descripcion": "Cambio de aceite",
  "estado": "TERMINADO",
  "costoFinal": 25000,
  "costoRepuestos": 2000,
  "fechaCreacion": "2026-11-06T10:00:00",
  "historial": [
    { "estado": "INGRESADO", "fechaHora": "2026-11-06T10:00:00" },
    { "estado": "EN_REPARACION", "fechaHora": "2026-11-06T10:05:00" },
    { "estado": "TERMINADO", "fechaHora": "2026-11-06T12:05:00" }
  ],
  "repuestos": [
    {
      "repuestoId": "UUID",
      "nombre": "Filtro de aceite",
      "cantidad": 2,
      "precioUnitario": 1000,
      "subtotal": 2000
    }
  ],
  "horasEnReparacion": 2.0
}
```

## Estructura del proyecto

Organización por capas con un núcleo de dominio (estilo hexagonal):

```
src/main/java/com/taller
├── core                      Dominio puro (no depende de Spring)
│   ├── models                Cliente, Vehiculo, Orden, Repuesto, ItemRepuesto,
│   │                         EstadoOrden (enum), CambioEstado
│   └── exceptions            Excepciones de dominio (Cliente, Vehiculo, Orden, Conflicto, NoEncontrado…)
└── adapter                   Infraestructura / entrada-salida
    ├── data
    │   ├── entity            DTO_* = entidades JPA (tablas)
    │   ├── jpa               JPA_* = interfaces de Spring Data
    │   ├── repository        Repository_* = acceso a datos
    │   ├── mapper            Mapper_* = conversión entidad ↔ dominio ↔ respuesta
    │   └── api               Api_Error = formato uniforme de error
    └── service
        ├── controller        Controller_* = endpoints REST
        ├── bean              Bean_* = servicios con reglas de negocio y @Transactional
        └── dto               Request_* / Response_* = contrato JSON de la API
```

Flujo de una petición, por ejemplo `POST /api/ordenes`:

```
Controller_Orden.crear
  → Bean_Orden.crear            reglas: vehículo existe y no tiene orden abierta
     → Orden.crear              valida datos, estado INGRESADO e historial inicial
     → Repository_Orden         → JPA_Orden → Hibernate → H2
  → Mapper_Orden.toResponse     arma el JSON de respuesta
```

Puntos clave:

- `core.models` no conoce JPA; `DTO_*` son las tablas y `Request_*`/`Response_*` el contrato HTTP.
- Las validaciones de formato/obligatorios viven en los modelos de dominio.
- Las reglas que necesitan datos (patente única, orden abierta, stock, facturación) viven en los `Bean_*`.
- Las transiciones de estado están en `EstadoOrden.puedeTransicionarA(...)`.
- `@RestControllerAdvice` traduce excepciones a `400`, `404`, `409` o `500` con un `Api_Error`.
- `@Transactional` en los servicios garantiza atomicidad y rollback.
