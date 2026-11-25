package cl.gaselvolcan.backend;


import java.util.*;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static cl.gaselvolcan.backend.Dtos.*;
@Service @Transactional(readOnly=true)
public class BoletaService {
 private final BoletaRepository boletas; private final ProductoRepository productos; private final UsuarioService usuarios;
 BoletaService(BoletaRepository b,ProductoRepository p,UsuarioService u){boletas=b;productos=p;usuarios=u;}
 private Boleta get(Long id){return boletas.findById(id).orElseThrow(()->ApiException.noExiste("Boleta"));}
 public List<BoletaOut> todas(){return boletas.findAllByOrderByIdDesc().stream().map(Mappers::boleta).toList();}
 public BoletaOut detalle(Long id){return Mappers.boleta(get(id));}
 public List<BoletaOut> propias(Long uid){return boletas.findByUsuarioIdOrderByIdDesc(uid).stream().map(Mappers::boleta).toList();}
 public BoletaOut propia(Long uid,Long id){Boleta b=get(id);if(!Objects.equals(b.usuario.getId(),uid))throw ApiException.noExiste("Boleta");return Mappers.boleta(b);}
 @Transactional public BoletaOut crear(Long uid,PedidoIn in){
  Usuario cliente=usuarios.get(uid);if(cliente.rol!=Rol.CLIENTE&&cliente.rol!=Rol.ADMINISTRADOR)throw ApiException.prohibido();
  Map<Long,Integer> cantidades=new TreeMap<>();for(Item i:in.items()){cantidades.merge(i.productoId(),i.cantidad(),Integer::sum);if(cantidades.get(i.productoId())>100)throw ApiException.invalido("Máximo 100 unidades por producto");}
  Boleta b=new Boleta();b.usuario=cliente;b.fecha=Instant.now();b.direccion=in.direccion().trim();b.telefono=in.telefono();b.estado=EstadoBoleta.PENDIENTE;
  for(var e:cantidades.entrySet()){
   Producto p=productos.bloquear(e.getKey()).orElseThrow(()->ApiException.noExiste("Producto"));int cantidad=e.getValue();
   if(!p.isActivo()||p.getStock()<cantidad)throw ApiException.conflicto("Stock insuficiente o producto inactivo: "+p.getNombre());
   p.setStock(p.getStock()-cantidad);DetalleBoleta d=new DetalleBoleta();d.boleta=b;d.producto=p;d.nombreProducto=p.getNombre();d.cantidad=cantidad;d.precioUnitario=p.getPrecio();d.subtotal=(long)cantidad*p.getPrecio();b.detalles.add(d);b.total+=d.subtotal;
  }
  return Mappers.boleta(boletas.saveAndFlush(b));
 }
 @Transactional public BoletaOut cambiarEstado(Long id,EstadoBoleta nuevo){
  Boleta b=boletas.bloquear(id).orElseThrow(()->ApiException.noExiste("Boleta"));if(b.estado==nuevo)return Mappers.boleta(b);
  boolean valido=switch(b.estado){case PENDIENTE -> nuevo==EstadoBoleta.CONFIRMADA||nuevo==EstadoBoleta.CANCELADA;case CONFIRMADA -> nuevo==EstadoBoleta.ENTREGADA||nuevo==EstadoBoleta.CANCELADA;default -> false;};
  if(!valido)throw ApiException.conflicto("Transición de estado no permitida");
  if(nuevo==EstadoBoleta.CANCELADA){Map<Long,Integer> devolver=new TreeMap<>();for(DetalleBoleta d:b.detalles)devolver.merge(d.producto.getId(),d.cantidad,Integer::sum);for(var e:devolver.entrySet()){Producto p=productos.bloquear(e.getKey()).orElseThrow(()->ApiException.noExiste("Producto"));p.setStock(Math.addExact(p.getStock(),e.getValue()));}}
  b.estado=nuevo;return Mappers.boleta(b);
 }
 @Transactional public void eliminar(Long id){Boleta b=boletas.bloquear(id).orElseThrow(()->ApiException.noExiste("Boleta"));if(b.estado!=EstadoBoleta.CANCELADA)throw ApiException.conflicto("Solo se pueden eliminar boletas canceladas");boletas.delete(b);}
}
