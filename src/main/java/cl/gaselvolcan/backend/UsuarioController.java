package cl.gaselvolcan.backend;


import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import static cl.gaselvolcan.backend.Dtos.*;
@RestController @RequestMapping("/api/v1/usuarios") @PreAuthorize("hasRole('ADMINISTRADOR')") @SecurityRequirement(name="bearerAuth") @Tag(name="Usuarios")
public class UsuarioController {
 private final UsuarioService s;UsuarioController(UsuarioService s){this.s=s;}
 @GetMapping public List<UsuarioOut> listar(){return s.listar();}
 @GetMapping("/{id}") public UsuarioOut detalle(@PathVariable Long id){return s.perfil(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public UsuarioOut crear(@Valid @RequestBody UsuarioIn in){return s.crear(in);}
 @PutMapping("/{id}") public UsuarioOut editar(@PathVariable Long id,@Valid @RequestBody UsuarioIn in){return s.editar(id,in);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id){s.eliminar(id);}
}
