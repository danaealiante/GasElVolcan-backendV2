package cl.gaselvolcan.backend;


import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import static cl.gaselvolcan.backend.Dtos.*;
@RestController @RequestMapping("/api/v1/tienda") @Tag(name="Tienda")
public class TiendaController {
 private final CatalogoService c;private final BoletaService b;TiendaController(CatalogoService c,BoletaService b){this.c=c;this.b=b;}
 @GetMapping("/productos") public List<ProductoOut> productos(){return c.productos(true);}
 @GetMapping("/productos/{id}") public ProductoOut producto(@PathVariable Long id){return c.productoOut(id,true);}
 @GetMapping("/categorias") public List<CategoriaOut> categorias(){return c.categorias();}
 @PostMapping("/pedidos") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('CLIENTE','ADMINISTRADOR')") @SecurityRequirement(name="bearerAuth") public BoletaOut crear(Authentication a,@Valid @RequestBody PedidoIn in){return b.crear(Long.valueOf(a.getName()),in);}
 @GetMapping("/pedidos") @PreAuthorize("hasAnyRole('CLIENTE','ADMINISTRADOR')") @SecurityRequirement(name="bearerAuth") public List<BoletaOut> propios(Authentication a){return b.propias(Long.valueOf(a.getName()));}
 @GetMapping("/pedidos/{id}") @PreAuthorize("hasAnyRole('CLIENTE','ADMINISTRADOR')") @SecurityRequirement(name="bearerAuth") public BoletaOut propio(Authentication a,@PathVariable Long id){return b.propia(Long.valueOf(a.getName()),id);}
}
