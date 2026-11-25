package cl.gaselvolcan.backend;


import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import static cl.gaselvolcan.backend.Dtos.*;
@RestController @RequestMapping("/api/v1/boletas") @Tag(name="Órdenes y boletas") @SecurityRequirement(name="bearerAuth")
public class BoletaController {
 private final BoletaService s;BoletaController(BoletaService s){this.s=s;}
 @GetMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')") public List<BoletaOut> listar(){return s.todas();}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')") public BoletaOut detalle(@PathVariable Long id){return s.detalle(id);}
 @PatchMapping("/{id}/estado") @PreAuthorize("hasRole('ADMINISTRADOR')") public BoletaOut estado(@PathVariable Long id,@Valid @RequestBody EstadoIn in){return s.cambiarEstado(id,in.estado());}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasRole('ADMINISTRADOR')") public void eliminar(@PathVariable Long id){s.eliminar(id);}
}
