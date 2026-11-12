# Taller Mecánico — API REST

API para seguir cada auto desde que ingresa al taller hasta que se entrega al cliente.

## Tecnologías

- Java 21
- Spring Boot 4.1.2
- Spring Data JPA / Hibernate
- H2 (base de datos en memoria)
- Maven

## Ejecutar

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.
La consola H2 está en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taller`, usuario `sa`, sin contraseña).

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
