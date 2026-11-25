# Integración con React

1. Configurar VITE_API_URL con la URL real (sin /api al final); reiniciar Vite.
2. Usar `api.js` para registrar y autenticar. Guardar el usuario devuelto por iniciarSesion en el estado React, nunca confiar en un rol editable por el usuario para proteger la API. El JWT se conserva en memoria; iniciar sesión otra vez después de recargar. Quitar credenciales hardcodeadas y login falso.
3. Mostrar `obtenerProductos` en el catálogo, `crearPedido({direccion, telefono, items: [{productoId, cantidad}]})` al confirmar carrito, y `misPedidos` en la tienda. Solo limpiar carrito si el POST fue exitoso; usar el total de la respuesta. Quitar datos ficticios de pedidos y fallback de productos.
4. Administrador: usar las funciones de gestión para CRUD de productos/categorías/usuarios y estados de boletas. Vendedor: solo `productosGestion`, `productoGestion`, `boletasGestion` y `boletaGestion` (no mostrar controles de escritura ni otros menús). Cliente: solo tienda y pedidos propios. Protege rutas de React según el usuario autenticado; la seguridad real está en el servidor.
5. En errores 401, cerrar sesión y solicitar login; en 403 mostrar acceso denegado; en 409 mostrar mensaje de conflicto. No mostrar “API conectada” ante un error de red.

Este archivo es un adaptador, no una modificación ya aplicada al frontend. El repositorio anterior requiere revisar sus componentes y realizar pruebas integradas.
