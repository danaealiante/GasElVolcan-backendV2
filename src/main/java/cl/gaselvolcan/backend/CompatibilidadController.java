package cl.gaselvolcan.backend;


import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.List;
import static cl.gaselvolcan.backend.Dtos.*;
// Solo lectura temporal: el frontend anterior solicita /api/productos.
@Hidden @RestController
public class CompatibilidadController {
 private final CatalogoService s;CompatibilidadController(CatalogoService s){this.s=s;}
 @Deprecated @GetMapping("/api/productos") public List<ProductoOut> productos(){return s.productos(true);}
}
