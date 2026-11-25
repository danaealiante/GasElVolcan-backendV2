package cl.gaselvolcan.backend;


import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import static cl.gaselvolcan.backend.Dtos.*;
@RestController @RequestMapping("/api/v1/auth") @Tag(name="Autenticación")
public class AuthController {
 private final UsuarioService service; AuthController(UsuarioService s){service=s;}
 @PostMapping("/registro") @ResponseStatus(HttpStatus.CREATED) public UsuarioOut registro(@Valid @RequestBody Registro in){return service.registro(in);}
 @PostMapping("/login") public Token login(@Valid @RequestBody Login in){return service.login(in);}
 @GetMapping("/me") @SecurityRequirement(name="bearerAuth") @PreAuthorize("hasAnyRole('CLIENTE','ADMINISTRADOR')") public UsuarioOut me(Authentication a){return service.perfil(Long.valueOf(a.getName()));}
}
