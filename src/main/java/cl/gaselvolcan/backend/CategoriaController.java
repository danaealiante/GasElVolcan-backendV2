package cl.gaselvolcan.backend;


import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import static cl.gaselvolcan.backend.Dtos.*;
@RestController @RequestMapping("/api/v1/categorias") @PreAuthorize("hasRole('ADMINISTRADOR')") @SecurityRequirement(name="bearerAuth") @Tag(name="Categorías")
public class CategoriaController {
 private final CatalogoService s;CategoriaController(CatalogoService s){this.s=s;}
 @GetMapping public List<CategoriaOut> listar(){return s.categorias();}
 @GetMapping("/{id}") public CategoriaOut detalle(@PathVariable Long id){return s.categoriaOut(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public CategoriaOut crear(@Valid @RequestBody CategoriaIn in){return s.crearCategoria(in);}
 @PutMapping("/{id}") public CategoriaOut editar(@PathVariable Long id,@Valid @RequestBody CategoriaIn in){return s.editarCategoria(id,in);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id){s.eliminarCategoria(id);}
}
