package cl.gaselvolcan.backend;


import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static cl.gaselvolcan.backend.Dtos.*;
@Service @Transactional(readOnly=true)
public class CatalogoService {
 private final CategoriaRepository categorias;private final ProductoRepository productos;private final DetalleBoletaRepository detalles;
 CatalogoService(CategoriaRepository c,ProductoRepository p,DetalleBoletaRepository d){categorias=c;productos=p;detalles=d;}
 Categoria categoria(Long id){return categorias.findById(id).orElseThrow(()->ApiException.noExiste("Categoría"));}
 Producto producto(Long id){return productos.findById(id).orElseThrow(()->ApiException.noExiste("Producto"));}
 public List<CategoriaOut> categorias(){return categorias.findAll().stream().map(Mappers::categoria).toList();}
 public CategoriaOut categoriaOut(Long id){return Mappers.categoria(categoria(id));}
 @Transactional public CategoriaOut crearCategoria(CategoriaIn in){if(categorias.existsByNombreIgnoreCase(in.nombre().trim()))throw ApiException.conflicto("Categoría existente");Categoria c=new Categoria();c.nombre=in.nombre().trim();c.descripcion=in.descripcion();return Mappers.categoria(categorias.save(c));}
 @Transactional public CategoriaOut editarCategoria(Long id,CategoriaIn in){Categoria c=categoria(id);if(!c.nombre.equalsIgnoreCase(in.nombre().trim())&&categorias.existsByNombreIgnoreCase(in.nombre().trim()))throw ApiException.conflicto("Categoría existente");c.nombre=in.nombre().trim();c.descripcion=in.descripcion();return Mappers.categoria(c);}
 @Transactional public void eliminarCategoria(Long id){Categoria c=categoria(id);if(productos.existsByCategoriaId(id))throw ApiException.conflicto("La categoría contiene productos");categorias.delete(c);}
 public List<ProductoOut> productos(boolean tienda){return (tienda?productos.findByActivoTrueOrderByIdAsc():productos.findAll()).stream().map(Mappers::producto).toList();}
 public ProductoOut productoOut(Long id,boolean tienda){Producto p=producto(id);if(tienda&&!p.isActivo())throw ApiException.noExiste("Producto");return Mappers.producto(p);}
 private void asignar(Producto p,ProductoIn in){p.actualizar(in.nombre().trim(),in.descripcion(),in.precio(),in.stock(),in.activo(),categoria(in.categoriaId()));}
 @Transactional public ProductoOut crearProducto(ProductoIn in){Producto p=new Producto();asignar(p,in);return Mappers.producto(productos.save(p));}
 @Transactional public ProductoOut editarProducto(Long id,ProductoIn in){Producto p=productos.bloquear(id).orElseThrow(()->ApiException.noExiste("Producto"));asignar(p,in);return Mappers.producto(p);}
 @Transactional public void eliminarProducto(Long id){Producto p=productos.bloquear(id).orElseThrow(()->ApiException.noExiste("Producto"));if(detalles.existsByProductoId(id)){p.setActivo(false);}else productos.delete(p);}
}
