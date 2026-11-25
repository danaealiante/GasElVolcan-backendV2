package cl.gaselvolcan.backend;

import static cl.gaselvolcan.backend.Dtos.*;
final class Mappers {
 private Mappers(){}
 static UsuarioOut usuario(Usuario u){return new UsuarioOut(u.id,u.nombre,u.email,u.telefono,u.rol,u.activo);}
 static CategoriaOut categoria(Categoria c){return new CategoriaOut(c.getId(),c.getNombre(),c.getDescripcion());}
 static ProductoOut producto(Producto p){return new ProductoOut(p.getId(),p.getNombre(),p.getDescripcion(),p.getPrecio(),p.getStock(),p.isActivo(),categoria(p.getCategoria()));}
 static BoletaOut boleta(Boleta b){return new BoletaOut(b.id,b.usuario.getId(),b.usuario.getNombre(),b.fecha,b.direccion,b.telefono,b.estado,b.total,b.detalles.stream().map(d->new DetalleOut(d.id,d.producto.getId(),d.nombreProducto,d.cantidad,d.precioUnitario,d.subtotal)).toList());}
}
