// Copiar a src/services/api.js del frontend real y adaptar sus componentes.
const base = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '');
let token = null; // Mantener en memoria; tras recargar, iniciar sesión nuevamente.
export function guardarToken(value) { token = value || null; }
export function cerrarSesion() { token = null; }
async function api(path, method = 'GET', data) {
  const headers = { Accept: 'application/json' };
  if (data !== undefined) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;
  const r = await fetch(`${base}/api/v1${path}`, { method, headers, ...(data !== undefined ? { body: JSON.stringify(data) } : {}) });
  if (!r.ok) {
    if (r.status === 401) cerrarSesion();
    const error = await r.json().catch(() => ({}));
    throw Object.assign(new Error(error.mensaje || `Error HTTP ${r.status}`), { status: r.status, campos: error.campos });
  }
  return r.status === 204 ? null : r.json();
}
export const registrar = data => api('/auth/registro', 'POST', data);
export async function iniciarSesion(email,password) {const r=await api('/auth/login','POST',{email,password});guardarToken(r.token);return r.usuario;}
export const perfil = () => api('/auth/me');
export const obtenerProductos = () => api('/tienda/productos');
export const obtenerProducto = id => api(`/tienda/productos/${id}`);
export const obtenerCategoriasTienda = () => api('/tienda/categorias');
export const crearPedido = data => api('/tienda/pedidos','POST',data);
export const misPedidos = () => api('/tienda/pedidos');
export const miPedido = id => api(`/tienda/pedidos/${id}`);
export const productosGestion = () => api('/productos');
export const productoGestion = id => api(`/productos/${id}`);
export const guardarProducto = (data,id) => api(id?`/productos/${id}`:'/productos',id?'PUT':'POST',data);
export const eliminarProducto = id => api(`/productos/${id}`,'DELETE');
export const categoriasGestion = () => api('/categorias');
export const guardarCategoria = (data,id) => api(id?`/categorias/${id}`:'/categorias',id?'PUT':'POST',data);
export const eliminarCategoria = id => api(`/categorias/${id}`,'DELETE');
export const usuariosGestion = () => api('/usuarios');
export const guardarUsuario = (data,id) => api(id?`/usuarios/${id}`:'/usuarios',id?'PUT':'POST',data);
export const eliminarUsuario = id => api(`/usuarios/${id}`,'DELETE');
export const boletasGestion = () => api('/boletas');
export const boletaGestion = id => api(`/boletas/${id}`);
export const cambiarEstado = (id,estado) => api(`/boletas/${id}/estado`,'PATCH',{estado});
export const eliminarBoleta = id => api(`/boletas/${id}`,'DELETE');
