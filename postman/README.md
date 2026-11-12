# Probar la API con Postman

## 1. Levantar la aplicación

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`. Verificá que responda:

```bash
curl http://localhost:8080/api/clientes
```

Debe devolver `[]` o una lista JSON.

## 2. Importar la colección

1. Abrí Postman.
2. Presioná **Import**.
3. Elegí el archivo `postman/Taller.postman_collection.json`.
4. La colección se llama **Taller Mecánico API**.

## 3. Revisar la variable `baseUrl`

En Postman, entrá a la colección → pestaña **Variables** y verificá:

| Variable | Valor |
|---|---|
| `baseUrl` | `http://localhost:8080` |

Las demás variables (`clienteId`, `vehiculoId`, `ordenId`, `repuestoId`, `patente`, etc.) se completan solas con los scripts de cada request.

## 4. Ejecutar el flujo principal

Tenés dos opciones:

### Opción A: ejecutar toda la colección

1. Seleccioná la colección **Taller Mecánico API**.
2. Presioná **Run**.
3. Ejecutá la carpeta **Flujo principal**.
4. Al terminar, ejecutá la carpeta **Prueba extra: entrega sin costo**.

Postman va a mostrar en verde las pruebas que pasan y en rojo las que fallan.

### Opción B: ejecutar request por request

Ejecutá los requests en este orden:

1. `1. Crear cliente`
2. `2. Crear vehículo`
3. `3. Crear orden`
4. `3b. Segunda orden abierta (409)`
5. `4. Crear repuesto`
6. `5. Agregar repuesto a la orden`
7. `5b. Costo final menor a repuestos (400)`
8. `5c. Stock insuficiente (409)`
9. `6. Ver orden`
10. `7. Ver vehículos del cliente`
11. `8. Historial por patente`
12. `9. Actualizar costo y pasar a EN_REPARACION`
13. `10. Pasar a TERMINADO`
14. `11. Entregar orden`
15. `12. Facturación del mes`
16. `13. Patente duplicada (409)`

Después, si querés, ejecutá la carpeta **Prueba extra: entrega sin costo**.

> Es importante ejecutarlos en orden porque los scripts guardan los IDs en variables y los requests siguientes los usan.

## 5. Qué hace cada request

| Request | Qué prueba |
|---|---|
| 1 | `POST /api/clientes` → 201 y guarda `clienteId` |
| 2 | `POST /api/vehiculos` → 201, valida patente y guarda `vehiculoId` |
| 3 | `POST /api/ordenes` → 201, estado `INGRESADO` y guarda `ordenId` |
| 3b | Segunda orden para el mismo vehículo → 409 |
| 4 | `POST /api/repuestos` → 201 y guarda `repuestoId` |
| 5 | `POST /api/ordenes/{id}/repuestos` → 200, descuenta stock |
| 5b | `PUT /api/ordenes/{id}` con costo menor al de repuestos → 400 |
| 5c | Agregar más cantidad que el stock → 409 |
| 6 | `GET /api/ordenes/{id}` |
| 7 | `GET /api/clientes/{id}/vehiculos` |
| 8 | `GET /api/vehiculos/patente/{patente}/historial` |
| 9 | Cargar `costoFinal` y pasar a `EN_REPARACION` |
| 10 | Pasar a `TERMINADO` y verificar `horasEnReparacion` |
| 11 | Pasar a `ENTREGADO` |
| 12 | `GET /api/ordenes/facturacion?mes=...` |
| 13 | Repetir la patente → 409 |
| 14-18 | Crear otro vehículo/orden y verificar que no se puede entregar sin costo → 409 |

## 6. Ver los IDs guardados

En Postman:

1. Seleccioná la colección **Taller Mecánico API**.
2. Entrá a la pestaña **Variables**.
3. Ahí vas a ver los valores actuales de `clienteId`, `vehiculoId`, `ordenId`, `repuestoId`, etc.

También podés verlos en la consola de Postman: **View → Show Postman Console**.

## 7. Solución de problemas

- **Connection refused / Could not send request**: la aplicación no está corriendo. Ejecutá `./mvnw spring-boot:run`.
- **404 en los requests siguientes**: no se ejecutó el request anterior o la variable está vacía. Ejecutá el flujo en orden.
- **409 al crear el vehículo**: la patente ya existe. La colección genera una patente nueva automáticamente, pero si ejecutaste un request suelto puede haber quedado una patente repetida. Reiniciá la app para limpiar la base H2 o cambiá el valor de `patente`.
- **La facturación da 0**: asegurate de haber entregado la orden (request 11) antes de ejecutar el request 12.
- **Querés empezar de cero**: detené la app y volvé a ejecutarla. La base H2 es en memoria, así que se reinicia.
