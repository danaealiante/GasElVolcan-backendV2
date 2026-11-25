package cl.gaselvolcan.backend;


import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import static cl.gaselvolcan.backend.Dtos.*;
@Configuration
public class DatosIniciales {
 @Bean ApplicationRunner inicio(UsuarioService usuarios,UsuarioRepository ur,CatalogoService catalogo,CategoriaRepository cr,ProductoRepository pr,
    @Value("${app.admin-email}") String admin,@Value("${app.admin-password}") String clave,@Value("${app.demo-data}") boolean demo){return args->{
   if(!admin.isBlank()&&!ur.existsByEmail(UsuarioService.email(admin))){if(clave.isBlank())throw new IllegalStateException("Falta ADMIN_PASSWORD");usuarios.crear(new UsuarioIn("Administrador",admin,clave,null,Rol.ADMINISTRADOR,true));}
   if(ur.countByRolAndActivoTrue(Rol.ADMINISTRADOR)==0)throw new IllegalStateException("Configura ADMIN_EMAIL y ADMIN_PASSWORD para crear el administrador inicial");
   if(demo){
    if(!ur.existsByEmail("vendedor@demo.local"))usuarios.crear(new UsuarioIn("Vendedor demo","vendedor@demo.local","DemoVenta2026!",null,Rol.VENDEDOR,true));
    if(!ur.existsByEmail("cliente@demo.local"))usuarios.crear(new UsuarioIn("Cliente demo","cliente@demo.local","DemoCliente2026!","912345678",Rol.CLIENTE,true));
    if(cr.count()==0){CategoriaOut cat=catalogo.crearCategoria(new CategoriaIn("Cilindros de gas","Gas licuado para el hogar"));catalogo.crearProducto(new ProductoIn("Cilindro de Gas 5 kg","Cilindro doméstico",10000,30,cat.id(),true));catalogo.crearProducto(new ProductoIn("Cilindro de Gas 15 kg","Cilindro doméstico",23500,30,cat.id(),true));catalogo.crearProducto(new ProductoIn("Cilindro de Gas 45 kg","Cilindro de mayor capacidad",65000,10,cat.id(),true));}
   }
 };}
}
