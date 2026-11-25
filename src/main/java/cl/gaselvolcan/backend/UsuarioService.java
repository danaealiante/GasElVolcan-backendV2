package cl.gaselvolcan.backend;


import java.util.*;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static cl.gaselvolcan.backend.Dtos.*;
@Service @Transactional(readOnly=true)
public class UsuarioService {
 private final UsuarioRepository repo; private final BoletaRepository boletas; private final PasswordEncoder encoder; private final JwtEncoder jwt; private final String issuer;
 UsuarioService(UsuarioRepository repo,BoletaRepository boletas,PasswordEncoder encoder,JwtEncoder jwt,@Value("${app.jwt-issuer}") String issuer){this.repo=repo;this.boletas=boletas;this.encoder=encoder;this.jwt=jwt;this.issuer=issuer;}
 static String email(String s){return s.trim().toLowerCase(Locale.ROOT);}
 Usuario get(Long id){return repo.findById(id).orElseThrow(()->ApiException.noExiste("Usuario"));}
 private String hash(String p){if(p==null||p.length()<8||p.getBytes(StandardCharsets.UTF_8).length>72)throw ApiException.invalido("La contraseña debe tener al menos 8 caracteres y máximo 72 bytes UTF-8");return encoder.encode(p);}
 private void emailLibre(String e,Long id){if(repo.findByEmail(e).filter(u->!Objects.equals(u.id,id)).isPresent())throw ApiException.conflicto("El correo ya está registrado");}
 @Transactional public UsuarioOut registro(Registro in){return crear(new UsuarioIn(in.nombre(),in.email(),in.password(),in.telefono(),Rol.CLIENTE,true));}
 public Token login(Login in){
  Usuario u=repo.findByEmail(email(in.email())).orElse(null);
  if(u==null||!u.activo||in.password().getBytes(StandardCharsets.UTF_8).length>72||!encoder.matches(in.password(),u.passwordHash))throw new ApiException(HttpStatus.UNAUTHORIZED,"Credenciales inválidas");
  Instant now=Instant.now(); JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).subject(u.id.toString()).issuedAt(now).expiresAt(now.plusSeconds(3600)).claim("rol",u.rol.name()).build();
  String token=jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue(); return new Token(token,"Bearer",3600,Mappers.usuario(u));
 }
 public UsuarioOut perfil(Long id){return Mappers.usuario(get(id));}
 public List<UsuarioOut> listar(){return repo.findAll().stream().map(Mappers::usuario).toList();}
 @Transactional public UsuarioOut crear(UsuarioIn in){Usuario u=new Usuario();u.nombre=in.nombre().trim();u.email=email(in.email());emailLibre(u.email,null);u.passwordHash=hash(in.password());u.telefono=in.telefono();u.rol=in.rol();u.activo=in.activo();return Mappers.usuario(repo.save(u));}
 @Transactional public UsuarioOut editar(Long id,UsuarioIn in){Usuario u=get(id);emailLibre(email(in.email()),id);
  if(u.rol==Rol.ADMINISTRADOR&&u.activo&&(in.rol()!=Rol.ADMINISTRADOR||!in.activo())&&repo.countByRolAndActivoTrue(Rol.ADMINISTRADOR)<=1)throw ApiException.conflicto("Debe existir un administrador activo");
  u.nombre=in.nombre().trim();u.email=email(in.email());u.telefono=in.telefono();u.rol=in.rol();u.activo=in.activo();if(in.password()!=null)u.passwordHash=hash(in.password());return Mappers.usuario(u);}
 @Transactional public void eliminar(Long id){Usuario u=get(id);if(u.rol==Rol.ADMINISTRADOR&&u.activo&&repo.countByRolAndActivoTrue(Rol.ADMINISTRADOR)<=1)throw ApiException.conflicto("No puedes eliminar el último administrador");if(boletas.existsByUsuarioId(id)){u.activo=false;}else repo.delete(u);}
}
