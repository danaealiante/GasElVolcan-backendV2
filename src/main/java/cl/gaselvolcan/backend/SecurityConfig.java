package cl.gaselvolcan.backend;


import java.security.SecureRandom;
import java.util.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import org.springframework.http.HttpMethod;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecretKey jwtKey(@Value("${app.jwt-secret}") String secret, Environment env){
  byte[] bytes;
  if(secret.isBlank()){
   if(!Arrays.asList(env.getActiveProfiles()).contains("demo")) throw new IllegalStateException("Configura JWT_SECRET (Base64, mínimo 32 bytes aleatorios)");
   bytes=new byte[32]; new SecureRandom().nextBytes(bytes);
  } else {bytes=Base64.getDecoder().decode(secret);}
  if(bytes.length<32) throw new IllegalStateException("JWT_SECRET debe tener mínimo 32 bytes");
  return new SecretKeySpec(bytes,"HmacSHA256");
 }
 @Bean JwtEncoder jwtEncoder(SecretKey key){return new NimbusJwtEncoder(new ImmutableSecret<>(key));}
 @Bean JwtDecoder jwtDecoder(SecretKey key,@Value("${app.jwt-issuer}") String issuer){
  NimbusJwtDecoder d=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  d.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer)); return d;
 }
 @Bean CorsConfigurationSource cors(@Value("${app.cors-origins}") String origins){
  CorsConfiguration c=new CorsConfiguration(); c.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isBlank()).toList());
  c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); c.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
  c.setAllowCredentials(false); c.setMaxAge(3600L); UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",c);return source;
 }
 @Bean SecurityFilterChain security(HttpSecurity http,UsuarioRepository usuarios,@org.springframework.beans.factory.annotation.Qualifier("cors") CorsConfigurationSource cors) throws Exception {
  // El rol y la vigencia se consultan en BD en cada solicitud; no se confía en roles obsoletos del token.
  var converter=(org.springframework.core.convert.converter.Converter<Jwt,JwtAuthenticationToken>) jwt -> {
   Usuario u=usuarios.findById(Long.valueOf(jwt.getSubject())).filter(x->x.activo).orElseThrow(()->new org.springframework.security.oauth2.core.OAuth2AuthenticationException(new org.springframework.security.oauth2.core.OAuth2Error("invalid_token")));
   return new JwtAuthenticationToken(jwt,List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_"+u.rol.name())),jwt.getSubject());
  };
  return http.csrf(c->c.disable()).cors(c->c.configurationSource(cors)).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(a->a.requestMatchers("/error","/actuator/health","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/","/index.html","/app.js","/styles.css").permitAll()
      .requestMatchers(HttpMethod.POST,"/api/v1/auth/login","/api/v1/auth/registro").permitAll()
      .requestMatchers(HttpMethod.GET,"/api/v1/tienda/productos","/api/v1/tienda/productos/*","/api/v1/tienda/categorias","/api/productos").permitAll()
      .requestMatchers("/api/v1/**").authenticated().anyRequest().denyAll())
    .oauth2ResourceServer(o->o.jwt(jwt->jwt.jwtAuthenticationConverter(converter))).build();
 }
 @Bean OpenAPI openAPI(){return new OpenAPI().info(new Info().title("Gas El Volcán API").version("v1").description("Catálogo, usuarios, boletas y control de acceso por roles"))
   .components(new Components().addSecuritySchemes("bearerAuth",new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));}
}
