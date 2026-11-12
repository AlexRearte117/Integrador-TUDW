const API = '/api';

const state = {
    clientes: [],
    vehiculos: [],
    repuestos: [],
    ordenes: []
};

let ordenActualId = null;
let msgTimer = null;

const moneyFmt = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    maximumFractionDigits: 2
});

function esc(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function money(value) {
    return value === null || value === undefined ? '—' : moneyFmt.format(Number(value));
}

function fecha(iso) {
    return iso ? new Date(iso).toLocaleString('es-AR') : '—';
}

async function api(path, options = {}) {
    const response = await fetch(API + path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    let data = null;
    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }

    if (!response.ok) {
        const message = (data && (data.message || data.error)) || `Error ${response.status}`;
        throw new Error(message);
    }

    return data;
}

function mostrarMensaje(texto, tipo = 'ok') {
    const el = document.getElementById('mensajes');
    el.textContent = texto;
    el.className = `mensajes ${tipo}`;
    clearTimeout(msgTimer);
    msgTimer = setTimeout(() => {
        el.textContent = '';
        el.className = 'mensajes';
    }, 4500);
}

function handleError(err) {
    mostrarMensaje(err.message || 'Ocurrió un error', 'error');
}

function setEstadoApi(ok) {
    const el = document.getElementById('estado-api');
    el.textContent = ok ? 'API conectada' : 'API sin conexión';
    el.className = `estado-api ${ok ? 'ok' : 'error'}`;
}

function setOptions(select, html) {
    const actual = select.value;
    select.innerHTML = html;
    if (actual) {
        select.value = actual;
    }
}

function nombreCliente(id) {
    const cliente = state.clientes.find(c => c.id === id);
    return cliente ? cliente.nombre : '—';
}

/* ------------------------- Carga y render ------------------------- */

async function cargarClientes() {
    state.clientes = await api('/clientes');
    renderClientes();
    fillClienteSelect();
}

async function cargarVehiculos() {
    state.vehiculos = await api('/vehiculos');
    renderVehiculos();
    fillVehiculoSelect();
}

async function cargarRepuestos() {
    state.repuestos = await api('/repuestos');
    renderRepuestos();
    fillRepuestoSelect();
}

async function cargarOrdenes() {
    const estado = document.getElementById('filtro-estado').value;
    const query = estado ? `?estado=${encodeURIComponent(estado)}` : '';
    state.ordenes = await api(`/ordenes${query}`);
    renderOrdenes();
}

async function cargarTodo() {
    const [clientes, vehiculos, repuestos, ordenes] = await Promise.all([
        api('/clientes'),
        api('/vehiculos'),
        api('/repuestos'),
        api('/ordenes')
    ]);

    state.clientes = clientes;
    state.vehiculos = vehiculos;
    state.repuestos = repuestos;
    state.ordenes = ordenes;

    renderClientes();
    fillClienteSelect();
    renderVehiculos();
    fillVehiculoSelect();
    renderRepuestos();
    fillRepuestoSelect();
    renderOrdenes();
}

function renderClientes() {
    const tbody = document.querySelector('#tabla-clientes tbody');
    tbody.innerHTML = state.clientes.length
        ? state.clientes.map(c => `
            <tr>
                <td>${esc(c.nombre)}</td>
                <td>${esc(c.telefono)}</td>
                <td class="acciones">
                    <button data-action="editar-cliente" data-id="${c.id}">Editar</button>
                    <button data-action="ver-vehiculos" data-id="${c.id}">Ver vehículos</button>
                    <button data-action="eliminar-cliente" data-id="${c.id}" class="peligro">Eliminar</button>
                </td>
            </tr>`).join('')
        : '<tr><td colspan="3" class="muted">Todavía no hay clientes.</td></tr>';
}

function renderVehiculos() {
    const tbody = document.querySelector('#tabla-vehiculos tbody');
    tbody.innerHTML = state.vehiculos.length
        ? state.vehiculos.map(v => `
            <tr>
                <td><strong>${esc(v.patente)}</strong></td>
                <td>${esc(v.marca)}</td>
                <td>${esc(v.modelo)}</td>
                <td>${esc(nombreCliente(v.clienteId))}</td>
                <td class="acciones">
                    <button data-action="editar-vehiculo" data-id="${v.id}">Editar</button>
                    <button data-action="eliminar-vehiculo" data-id="${v.id}" class="peligro">Eliminar</button>
                </td>
            </tr>`).join('')
        : '<tr><td colspan="5" class="muted">Todavía no hay vehículos.</td></tr>';
}

function renderRepuestos() {
    const tbody = document.querySelector('#tabla-repuestos tbody');
    tbody.innerHTML = state.repuestos.length
        ? state.repuestos.map(r => `
            <tr>
                <td>${esc(r.nombre)}</td>
                <td>${money(r.precio)}</td>
                <td>${r.stock}</td>
                <td class="acciones">
                    <button data-action="editar-repuesto" data-id="${r.id}">Editar</button>
                    <button data-action="eliminar-repuesto" data-id="${r.id}" class="peligro">Eliminar</button>
                </td>
            </tr>`).join('')
        : '<tr><td colspan="4" class="muted">Todavía no hay repuestos.</td></tr>';
}

function renderOrdenes() {
    const tbody = document.querySelector('#tabla-ordenes tbody');
    tbody.innerHTML = state.ordenes.length
        ? state.ordenes.map(o => `
            <tr>
                <td><strong>${esc(o.patente || '—')}</strong></td>
                <td>${esc(o.descripcion)}</td>
                <td><span class="badge estado-${o.estado.toLowerCase()}">${o.estado}</span></td>
                <td>${money(o.costoRepuestos)}</td>
                <td>${money(o.costoFinal)}</td>
                <td>${o.horasEnReparacion !== null && o.horasEnReparacion !== undefined ? o.horasEnReparacion + ' h' : '—'}</td>
                <td><button data-action="abrir-orden" data-id="${o.id}" class="primario">Abrir</button></td>
            </tr>`).join('')
        : '<tr><td colspan="7" class="muted">No hay órdenes para mostrar.</td></tr>';
}

function fillClienteSelect() {
    const select = document.querySelector('#form-vehiculo select[name="clienteId"]');
    if (!state.clientes.length) {
        select.innerHTML = '<option value="">Primero crea un cliente</option>';
        return;
    }
    setOptions(select, state.clientes
        .map(c => `<option value="${c.id}">${esc(c.nombre)}</option>`)
        .join(''));
}

function fillVehiculoSelect() {
    const select = document.querySelector('#form-orden select[name="vehiculoId"]');
    if (!state.vehiculos.length) {
        select.innerHTML = '<option value="">Primero crea un vehículo</option>';
        return;
    }
    setOptions(select, state.vehiculos
        .map(v => `<option value="${v.id}">${esc(v.patente)} — ${esc(v.marca)} ${esc(v.modelo)}</option>`)
        .join(''));
}

function fillRepuestoSelect() {
    const select = document.getElementById('dlg-repuesto');
    if (!state.repuestos.length) {
        select.innerHTML = '<option value="">Sin repuestos cargados</option>';
        return;
    }
    setOptions(select, state.repuestos
        .map(r => `<option value="${r.id}">${esc(r.nombre)} (stock ${r.stock}) — ${money(r.precio)}</option>`)
        .join(''));
}

/* ------------------------- Edición ------------------------- */

function editarCliente(id) {
    const cliente = state.clientes.find(c => c.id === id);
    if (!cliente) return;
    const form = document.getElementById('form-cliente');
    form.elements['registroId'].value = cliente.id;
    form.elements['nombre'].value = cliente.nombre;
    form.elements['telefono'].value = cliente.telefono;
    document.querySelector('[data-cancelar="cliente"]').hidden = false;
    form.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function editarVehiculo(id) {
    const vehiculo = state.vehiculos.find(v => v.id === id);
    if (!vehiculo) return;
    const form = document.getElementById('form-vehiculo');
    form.elements['registroId'].value = vehiculo.id;
    form.elements['patente'].value = vehiculo.patente;
    form.elements['marca'].value = vehiculo.marca;
    form.elements['modelo'].value = vehiculo.modelo;
    form.elements['clienteId'].value = vehiculo.clienteId;
    document.querySelector('[data-cancelar="vehiculo"]').hidden = false;
    form.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function editarRepuesto(id) {
    const repuesto = state.repuestos.find(r => r.id === id);
    if (!repuesto) return;
    const form = document.getElementById('form-repuesto');
    form.elements['registroId'].value = repuesto.id;
    form.elements['nombre'].value = repuesto.nombre;
    form.elements['precio'].value = repuesto.precio;
    form.elements['stock'].value = repuesto.stock;
    document.querySelector('[data-cancelar="repuesto"]').hidden = false;
    form.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/* ------------------------- Acciones de tabla ------------------------- */

async function eliminarCliente(id) {
    if (!confirm('¿Eliminar este cliente?')) return;
    await api(`/clientes/${id}`, { method: 'DELETE' });
    await cargarClientes();
    document.getElementById('card-vehiculos-cliente').hidden = true;
    mostrarMensaje('Cliente eliminado');
}

async function eliminarVehiculo(id) {
    if (!confirm('¿Eliminar este vehículo?')) return;
    await api(`/vehiculos/${id}`, { method: 'DELETE' });
    await cargarVehiculos();
    mostrarMensaje('Vehículo eliminado');
}

async function eliminarRepuesto(id) {
    if (!confirm('¿Eliminar este repuesto?')) return;
    await api(`/repuestos/${id}`, { method: 'DELETE' });
    await cargarRepuestos();
    mostrarMensaje('Repuesto eliminado');
}

async function verVehiculosCliente(id) {
    const cliente = state.clientes.find(c => c.id === id);
    const vehiculos = await api(`/clientes/${id}/vehiculos`);
    const contenedor = document.getElementById('lista-vehiculos-cliente');

    document.getElementById('cliente-seleccionado').textContent = cliente ? `— ${cliente.nombre}` : '';
    document.getElementById('card-vehiculos-cliente').hidden = false;

    contenedor.innerHTML = vehiculos.length
        ? `<div class="tabla-scroll"><table class="mini">
                <thead><tr><th>Patente</th><th>Marca</th><th>Modelo</th></tr></thead>
                <tbody>${vehiculos.map(v => `<tr><td>${esc(v.patente)}</td><td>${esc(v.marca)}</td><td>${esc(v.modelo)}</td></tr>`).join('')}</tbody>
            </table></div>`
        : '<p class="muted">El cliente no tiene vehículos.</p>';

    document.getElementById('card-vehiculos-cliente').scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/* ------------------------- Órdenes ------------------------- */

function renderDetalleOrden(orden) {
    document.getElementById('dlg-patente').textContent = orden.patente ? `— ${orden.patente}` : '';

    const historial = orden.historial.map(h => `
        <li><span class="badge estado-${h.estado.toLowerCase()}">${h.estado}</span><small>${fecha(h.fechaHora)}</small></li>`).join('');

    const repuestos = orden.repuestos.length
        ? `<div class="tabla-scroll"><table class="mini">
                <thead><tr><th>Repuesto</th><th>Cant.</th><th>P. unitario</th><th>Subtotal</th></tr></thead>
                <tbody>${orden.repuestos.map(r => `
                    <tr><td>${esc(r.nombre)}</td><td>${r.cantidad}</td><td>${money(r.precioUnitario)}</td><td>${money(r.subtotal)}</td></tr>`).join('')}
                </tbody>
            </table></div>`
        : '<p class="muted">Sin repuestos cargados.</p>';

    document.getElementById('dlg-detalle').innerHTML = `
        <p><strong>Descripción:</strong> ${esc(orden.descripcion)}</p>
        <p><strong>Estado:</strong> <span class="badge estado-${orden.estado.toLowerCase()}">${orden.estado}</span></p>
        <p><strong>Costo repuestos:</strong> ${money(orden.costoRepuestos)} · <strong>Costo final:</strong> ${money(orden.costoFinal)}</p>
        <p><strong>Horas en reparación:</strong> ${orden.horasEnReparacion !== null && orden.horasEnReparacion !== undefined ? orden.horasEnReparacion + ' h' : '—'}</p>
        <h4>Historial de estados</h4>
        <ul class="timeline">${historial}</ul>
        <h4>Repuestos</h4>
        ${repuestos}`;

    document.getElementById('dlg-estado').value = orden.estado;
    document.getElementById('dlg-costo').value = orden.costoFinal ?? '';
}

async function abrirOrden(id) {
    const orden = await api(`/ordenes/${id}`);
    ordenActualId = id;
    renderDetalleOrden(orden);
    const dialogo = document.getElementById('dialogo-orden');
    if (!dialogo.open) {
        dialogo.showModal();
    }
}

/* ------------------------- Eventos ------------------------- */

document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.tab').forEach(t => t.classList.toggle('activo', t === tab));
        document.querySelectorAll('.panel').forEach(p => p.classList.toggle('activo', p.id === `panel-${tab.dataset.panel}`));

        const panel = tab.dataset.panel;
        if (panel === 'clientes') cargarClientes().catch(handleError);
        if (panel === 'vehiculos') cargarVehiculos().catch(handleError);
        if (panel === 'repuestos') cargarRepuestos().catch(handleError);
        if (panel === 'ordenes') cargarOrdenes().catch(handleError);
    });
});

document.querySelectorAll('[data-cancelar]').forEach(boton => {
    boton.addEventListener('click', () => {
        const form = boton.closest('form');
        form.reset();
        form.elements['registroId'].value = '';
        boton.hidden = true;
    });
});

document.getElementById('form-cliente').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.target;
    const id = form.elements['registroId'].value;
    const body = {
        nombre: form.elements['nombre'].value.trim(),
        telefono: form.elements['telefono'].value.trim()
    };

    try {
        await api(id ? `/clientes/${id}` : '/clientes', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(body)
        });
        form.reset();
        form.elements['registroId'].value = '';
        document.querySelector('[data-cancelar="cliente"]').hidden = true;
        await cargarClientes();
        mostrarMensaje(id ? 'Cliente actualizado' : 'Cliente creado');
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('form-vehiculo').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.target;
    const id = form.elements['registroId'].value;
    const clienteId = form.elements['clienteId'].value;

    if (!clienteId) {
        mostrarMensaje('Primero crea un cliente para poder cargar un vehículo', 'error');
        return;
    }

    const body = {
        patente: form.elements['patente'].value.trim(),
        marca: form.elements['marca'].value.trim(),
        modelo: form.elements['modelo'].value.trim(),
        clienteId
    };

    try {
        await api(id ? `/vehiculos/${id}` : '/vehiculos', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(body)
        });
        form.reset();
        form.elements['registroId'].value = '';
        document.querySelector('[data-cancelar="vehiculo"]').hidden = true;
        await cargarVehiculos();
        mostrarMensaje(id ? 'Vehículo actualizado' : 'Vehículo creado');
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('form-repuesto').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.target;
    const id = form.elements['registroId'].value;
    const body = {
        nombre: form.elements['nombre'].value.trim(),
        precio: Number(form.elements['precio'].value),
        stock: Number(form.elements['stock'].value)
    };

    try {
        await api(id ? `/repuestos/${id}` : '/repuestos', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(body)
        });
        form.reset();
        form.elements['registroId'].value = '';
        document.querySelector('[data-cancelar="repuesto"]').hidden = true;
        await cargarRepuestos();
        mostrarMensaje(id ? 'Repuesto actualizado' : 'Repuesto creado');
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('form-orden').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.target;
    const vehiculoId = form.elements['vehiculoId'].value;

    if (!vehiculoId) {
        mostrarMensaje('Primero crea un vehículo para poder cargar una orden', 'error');
        return;
    }

    const body = {
        vehiculoId,
        descripcion: form.elements['descripcion'].value.trim()
    };

    try {
        await api('/ordenes', { method: 'POST', body: JSON.stringify(body) });
        form.reset();
        await cargarOrdenes();
        mostrarMensaje('Orden creada en estado INGRESADO');
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('form-historial').addEventListener('submit', async (event) => {
    event.preventDefault();
    const patente = event.target.elements['patente'].value.trim().toUpperCase();
    const contenedor = document.getElementById('historial-resultado');

    try {
        const ordenes = await api(`/vehiculos/patente/${encodeURIComponent(patente)}/historial`);
        contenedor.innerHTML = ordenes.length
            ? `<p class="muted">${ordenes.length} orden(es) de ${esc(patente)}, de la más nueva a la más vieja.</p>
               <div class="tabla-scroll"><table class="mini">
                    <thead><tr><th>Fecha</th><th>Descripción</th><th>Estado</th><th>Costo final</th><th>Horas</th></tr></thead>
                    <tbody>${ordenes.map(o => `
                        <tr>
                            <td>${fecha(o.fechaCreacion)}</td>
                            <td>${esc(o.descripcion)}</td>
                            <td><span class="badge estado-${o.estado.toLowerCase()}">${o.estado}</span></td>
                            <td>${money(o.costoFinal)}</td>
                            <td>${o.horasEnReparacion !== null && o.horasEnReparacion !== undefined ? o.horasEnReparacion + ' h' : '—'}</td>
                        </tr>`).join('')}
                    </tbody>
               </table></div>`
            : `<p class="muted">El vehículo ${esc(patente)} no tiene órdenes.</p>`;
    } catch (err) {
        contenedor.innerHTML = '';
        handleError(err);
    }
});

document.getElementById('form-facturacion').addEventListener('submit', async (event) => {
    event.preventDefault();
    const mes = event.target.elements['mes'].value;
    const contenedor = document.getElementById('facturacion-resultado');

    try {
        const facturacion = await api(`/ordenes/facturacion?mes=${encodeURIComponent(mes)}`);
        contenedor.innerHTML = `
            <div class="stat">
                <div class="valor">${facturacion.cantidadOrdenes}</div>
                <div class="etiqueta">Órdenes entregadas</div>
            </div>
            <div class="stat">
                <div class="valor">${money(facturacion.totalFacturado)}</div>
                <div class="etiqueta">Total facturado (${esc(facturacion.mes)})</div>
            </div>`;
    } catch (err) {
        contenedor.innerHTML = '';
        handleError(err);
    }
});

document.getElementById('filtro-estado').addEventListener('change', () => {
    cargarOrdenes().catch(handleError);
});

document.querySelector('main').addEventListener('click', async (event) => {
    const boton = event.target.closest('button[data-action]');
    if (!boton) return;

    const { action, id } = boton.dataset;

    try {
        if (action === 'editar-cliente') editarCliente(id);
        if (action === 'editar-vehiculo') editarVehiculo(id);
        if (action === 'editar-repuesto') editarRepuesto(id);
        if (action === 'eliminar-cliente') await eliminarCliente(id);
        if (action === 'eliminar-vehiculo') await eliminarVehiculo(id);
        if (action === 'eliminar-repuesto') await eliminarRepuesto(id);
        if (action === 'ver-vehiculos') await verVehiculosCliente(id);
        if (action === 'abrir-orden') await abrirOrden(id);
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('dlg-cerrar').addEventListener('click', () => {
    document.getElementById('dialogo-orden').close();
});

document.getElementById('dlg-guardar-estado').addEventListener('click', async () => {
    const estado = document.getElementById('dlg-estado').value;
    try {
        await api(`/ordenes/${ordenActualId}/estado`, {
            method: 'PUT',
            body: JSON.stringify({ estado })
        });
        await cargarOrdenes();
        await abrirOrden(ordenActualId);
        mostrarMensaje('Estado actualizado');
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('dlg-guardar-costo').addEventListener('click', async () => {
    const valor = document.getElementById('dlg-costo').value;
    if (valor === '') {
        mostrarMensaje('Ingresá un costo final', 'error');
        return;
    }

    try {
        await api(`/ordenes/${ordenActualId}`, {
            method: 'PUT',
            body: JSON.stringify({ costoFinal: Number(valor) })
        });
        await cargarOrdenes();
        await abrirOrden(ordenActualId);
        mostrarMensaje('Costo final actualizado');
    } catch (err) {
        handleError(err);
    }
});

document.getElementById('dlg-agregar-repuesto').addEventListener('click', async () => {
    const repuestoId = document.getElementById('dlg-repuesto').value;
    const cantidad = Number(document.getElementById('dlg-cantidad').value);

    if (!repuestoId) {
        mostrarMensaje('No hay repuestos cargados', 'error');
        return;
    }

    try {
        await api(`/ordenes/${ordenActualId}/repuestos`, {
            method: 'POST',
            body: JSON.stringify({ repuestoId, cantidad })
        });
        await Promise.all([cargarRepuestos(), cargarOrdenes()]);
        await abrirOrden(ordenActualId);
        mostrarMensaje('Repuesto agregado y stock descontado');
    } catch (err) {
        handleError(err);
    }
});

async function iniciar() {
    try {
        await cargarTodo();
        setEstadoApi(true);
    } catch (err) {
        setEstadoApi(false);
        mostrarMensaje(`No se pudo conectar con la API: ${err.message}`, 'error');
    }
}

iniciar();
