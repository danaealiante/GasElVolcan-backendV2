package cl.gaselvolcan.backend;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ApiIntegrationTest {
 @Autowired MockMvc mvc;@Autowired ObjectMapper json;@Autowired UsuarioRepository usuarios;
 String body(Object o)throws Exception{return json.writeValueAsString(o);}
 String login(String email,String clave)throws Exception{return "Bearer "+json.readTree(mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body(Map.of("email",email,"password",clave)))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("token").asText();}
 JsonNode crear(String url,String token,Object data)throws Exception{return json.readTree(mvc.perform(post(url).header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(body(data))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());}
 @Test void flujoCompletoYPermisos()throws Exception{
  mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body(Map.of("email","admin@test.local","password","incorrecta")))).andExpect(status().isUnauthorized());
  String admin=login("admin@test.local","TestAdmin2026!");
  mvc.perform(get("/api/v1/usuarios")).andExpect(status().isUnauthorized());
  mvc.perform(get("/api/v1/usuarios").header("Authorization","Bearer falso")).andExpect(status().isUnauthorized());
  long cat=crear("/api/v1/categorias",admin,Map.of("nombre","Gas","descripcion","Cilindros")).get("id").asLong();
  var producto=Map.of("nombre","Gas 15 kg","descripcion","Cilindro","precio",23500,"stock",8,"categoriaId",cat,"activo",true);
  long p=crear("/api/v1/productos",admin,producto).get("id").asLong();
  mvc.perform(get("/api/v1/tienda/productos/"+p)).andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(23500));
  mvc.perform(put("/api/v1/productos/"+p).header("Authorization",admin).contentType(MediaType.APPLICATION_JSON).content(body(producto))).andExpect(status().isOk());
  var vendedor=Map.of("nombre","Vendedor","email","ventas@test.local","password","Venta12345!","rol","VENDEDOR","activo",true);
  long v=crear("/api/v1/usuarios",admin,vendedor).get("id").asLong();
  String venta=login("ventas@test.local","Venta12345!");
  mvc.perform(get("/api/v1/productos").header("Authorization",venta)).andExpect(status().isOk());
  mvc.perform(post("/api/v1/productos").header("Authorization",venta).contentType(MediaType.APPLICATION_JSON).content(body(producto))).andExpect(status().isForbidden());
  mvc.perform(get("/api/v1/usuarios").header("Authorization",venta)).andExpect(status().isForbidden());
  mvc.perform(get("/api/v1/categorias").header("Authorization",venta)).andExpect(status().isForbidden());
  var cliente=Map.of("nombre","Cliente","email","cliente@test.local","password","Cliente12345!","telefono","912345678");
  mvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON).content(body(cliente))).andExpect(status().isCreated()).andExpect(jsonPath("$.rol").value("CLIENTE")).andExpect(jsonPath("$.passwordHash").doesNotExist());
  assertNotEquals("Cliente12345!",usuarios.findByEmail("cliente@test.local").orElseThrow().passwordHash);
  mvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON).content(body(cliente))).andExpect(status().isConflict());
  mvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"X\",\"email\":\"x@test.local\",\"password\":\"abcdefgh\",\"rol\":\"ADMINISTRADOR\"}")).andExpect(status().isBadRequest());
  String cli=login("cliente@test.local","Cliente12345!");
  mvc.perform(get("/api/v1/productos").header("Authorization",cli)).andExpect(status().isForbidden());
  mvc.perform(get("/api/v1/boletas").header("Authorization",cli)).andExpect(status().isForbidden());
  var pedido=Map.of("direccion","Av. Principal 123","telefono","912345678","items",java.util.List.of(Map.of("productoId",p,"cantidad",2)));
  mvc.perform(post("/api/v1/tienda/pedidos").header("Authorization",venta).contentType(MediaType.APPLICATION_JSON).content(body(pedido))).andExpect(status().isForbidden());
  var sinStock=Map.of("direccion","Av. Principal 123","telefono","912345678","items",java.util.List.of(Map.of("productoId",p,"cantidad",9)));
  mvc.perform(post("/api/v1/tienda/pedidos").header("Authorization",cli).contentType(MediaType.APPLICATION_JSON).content(body(sinStock))).andExpect(status().isConflict());
  mvc.perform(get("/api/v1/tienda/productos/"+p)).andExpect(jsonPath("$.stock").value(8));
  long b=crear("/api/v1/tienda/pedidos",cli,pedido).get("id").asLong();
  mvc.perform(get("/api/v1/tienda/pedidos/"+b).header("Authorization",cli)).andExpect(status().isOk()).andExpect(jsonPath("$.total").value(47000)).andExpect(jsonPath("$.detalles[0].cantidad").value(2));
  mvc.perform(get("/api/v1/tienda/productos/"+p)).andExpect(jsonPath("$.stock").value(6));
  mvc.perform(get("/api/v1/boletas/"+b).header("Authorization",venta)).andExpect(status().isOk());
  var otro=Map.of("nombre","Otro","email","otro@test.local","password","OtroCliente123!");
  mvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON).content(body(otro))).andExpect(status().isCreated());
  String segundo=login("otro@test.local","OtroCliente123!");
  mvc.perform(get("/api/v1/tienda/pedidos/"+b).header("Authorization",segundo)).andExpect(status().isNotFound());
  mvc.perform(patch("/api/v1/boletas/"+b+"/estado").header("Authorization",venta).contentType(MediaType.APPLICATION_JSON).content(body(Map.of("estado","CANCELADA")))).andExpect(status().isForbidden());
  mvc.perform(patch("/api/v1/boletas/"+b+"/estado").header("Authorization",admin).contentType(MediaType.APPLICATION_JSON).content(body(Map.of("estado","CANCELADA")))).andExpect(status().isOk());
  mvc.perform(get("/api/v1/tienda/productos/"+p)).andExpect(jsonPath("$.stock").value(8));
  mvc.perform(delete("/api/v1/boletas/"+b).header("Authorization",admin)).andExpect(status().isNoContent());
  mvc.perform(delete("/api/v1/productos/"+p).header("Authorization",admin)).andExpect(status().isNoContent());
  mvc.perform(delete("/api/v1/categorias/"+cat).header("Authorization",admin)).andExpect(status().isNoContent());
  mvc.perform(delete("/api/v1/usuarios/"+v).header("Authorization",admin)).andExpect(status().isNoContent());
  mvc.perform(get("/api/v1/productos").header("Authorization",venta)).andExpect(status().isUnauthorized());
  mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
  mvc.perform(get("/actuator/health")).andExpect(status().isOk());
 }
}
