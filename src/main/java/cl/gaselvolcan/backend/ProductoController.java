package cl.gaselvolcan.backend;


import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import static cl.gaselvolcan.backend.Dtos.*;
@RestController @RequestMapping("/api/v1/productos") @SecurityRequirement(name="bearerAuth") @Tag(name="Productos - gestión")
public class ProductoController {
 private final CatalogoService s;ProductoController(CatalogoService s){this.s=s;}
 @GetMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')") public List<ProductoOut> listar(){return s.productos(false);}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')") public ProductoOut detalle(@PathVariable Long id){return s.productoOut(id,false);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('ADMINISTRADOR')") public ProductoOut crear(@Valid @RequestBody ProductoIn in){return s.crearProducto(in);}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')") public ProductoOut editar(@PathVariable Long id,@Valid @RequestBody ProductoIn in){return s.editarProducto(id,in);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasRole('ADMINISTRADOR')") public void eliminar(@PathVariable Long id){s.eliminarProducto(id);}
}
