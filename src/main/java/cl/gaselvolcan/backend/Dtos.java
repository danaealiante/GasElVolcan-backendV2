package cl.gaselvolcan.backend;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
public final class Dtos {
 private Dtos() {}
 public record Registro(@NotBlank @Size(max=100) String nombre,@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(min=8,max=72) String password,@Pattern(regexp="^\\+?[0-9]{8,12}$") String telefono) {}
 public record Login(@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(max=72) String password) {}
 public record UsuarioIn(@NotBlank @Size(max=100) String nombre,@NotBlank @Email @Size(max=180) String email,@Size(min=8,max=72) String password,@Pattern(regexp="^\\+?[0-9]{8,12}$") String telefono,@NotNull Rol rol,@NotNull Boolean activo) {}
 public record UsuarioOut(Long id,String nombre,String email,String telefono,Rol rol,boolean activo) {}
 public record Token(String token,String tipo,long expiraEn,UsuarioOut usuario) {}
 public record CategoriaIn(@NotBlank @Size(max=100) String nombre,@Size(max=300) String descripcion) {}
 public record CategoriaOut(Long id,String nombre,String descripcion) {}
 public record ProductoIn(@NotBlank @Size(max=120) String nombre,@Size(max=400) String descripcion,@NotNull @Min(1) @Max(10000000) Integer precio,@NotNull @Min(0) @Max(1000000) Integer stock,@NotNull @Positive Long categoriaId,@NotNull Boolean activo) {}
 public record ProductoOut(Long id,String nombre,String descripcion,int precio,int stock,boolean activo,CategoriaOut categoria) {}
 public record Item(@NotNull @Positive Long productoId,@NotNull @Min(1) @Max(100) Integer cantidad) {}
 public record PedidoIn(@NotBlank @Size(max=250) String direccion,@NotBlank @Pattern(regexp="^\\+?[0-9]{8,12}$") String telefono,@NotEmpty @Size(max=50) List<@NotNull @Valid Item> items) {}
 public record EstadoIn(@NotNull EstadoBoleta estado) {}
 public record DetalleOut(Long id,Long productoId,String nombre,int cantidad,int precioUnitario,long subtotal) {}
 public record BoletaOut(Long id,Long usuarioId,String cliente,Instant fecha,String direccion,String telefono,EstadoBoleta estado,long total,List<DetalleOut> detalles) {}
}
