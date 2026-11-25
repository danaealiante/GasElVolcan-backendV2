# Gas El Volcán — Backend v2

Implementación independiente preparada para revisar la Evaluación Parcial 3 (DSY1104). Java 17, Spring Boot 3.5.6, Maven, Spring Data JPA, MySQL, Spring Security y JWT HS256, OpenAPI/Swagger. No contiene un repositorio .git ni historial de terceros.

**Estado:** código y pruebas escritos; compilación, ejecución, MySQL real, CI, integración con el React anterior y despliegue AWS todavía no verificados. No afirmar que la pauta está aprobada hasta realizar las comprobaciones.

## 1. Abrir en IntelliJ IDEA

1. Descomprime el ZIP en una carpeta nueva, distinta del proyecto anterior.
2. File > Open > selecciona `pom.xml` > Open as Project. Configura Project SDK Java 17 (o 21 con compilación objetivo 17). Espera que Maven descargue las dependencias. IntelliJ incluye Maven integrado: no requiere instalación externa para usar su panel Maven.
3. En la ventana Maven, ejecuta Lifecycle > `test`. También puedes ejecutar la clase `ApiIntegrationTest` desde IntelliJ. Confirma que todas las pruebas pasen. El perfil `test` usa H2; no cambia tu base anterior.
4. En Run > Edit Configurations, crea una configuración Application para `cl.gaselvolcan.backend.GasElVolcanApplication`. Para una demostración local, agrega la variable `SPRING_PROFILES_ACTIVE=demo`. Run. El perfil demo usa una base H2 persistente en `local-data/` y escucha solo en localhost. No es evidencia de MySQL.
5. Abre http://localhost:8080/swagger-ui/index.html y http://localhost:8080/actuator/health . Usa `/api/v1/auth/login`, copia `token` y pulsa Authorize en Swagger (pega el token sin escribir Bearer).

### Cuentas de demostración (solo perfil demo en localhost)

| Rol | Email | Clave |
|---|---|---|
| ADMINISTRADOR | admin@demo.local | DemoAdmin2026! |
| VENDEDOR | vendedor@demo.local | DemoVenta2026! |
| CLIENTE | cliente@demo.local | DemoCliente2026! |

En el perfil demo se crean tres productos ficticios. Nunca usar estas credenciales en AWS. El secreto JWT del perfil demo se genera aleatoriamente al iniciar si no se proporciona; reiniciar invalida tokens anteriores.

## 2. Ejecutar con MySQL real

Ejecuta `db/crear-base.sql` como administrador MySQL y crea un usuario restringido a la base nueva. En IntelliJ, desactiva el perfil demo y establece las variables de entorno en Run Configuration (no en Git):

- `DB_URL=jdbc:mysql://localhost:3306/gaselvolcan_v2?serverTimezone=UTC`
- `DB_USER` y `DB_PASSWORD`: credenciales reales de tu nueva base.
- `JWT_SECRET`: Base64 de 32 bytes aleatorios o más. En PowerShell: `$b=New-Object byte[] 32; $rng=[System.Security.Cryptography.RandomNumberGenerator]::Create(); $rng.GetBytes($b); [Convert]::ToBase64String($b)`.
- `ADMIN_EMAIL` y `ADMIN_PASSWORD`: crea el primer administrador. Usa un correo y clave propios de al menos 8 caracteres y máximo 72 bytes. No usar cuentas demo. No compartir la clave JWT ni las credenciales.
- `CORS_ORIGINS=http://localhost:5173` (separa múltiples orígenes exactos por comas).

En la primera ejecución el administrador se crea si no existe; se exige al menos uno activo. JPA crea/actualiza las tablas. Para AWS, usa una base **nueva**, no apuntes a la tabla producto incompleta anterior. Configura las variables en EC2 y crea la base en RDS. Para producción real, usa HTTPS, migraciones versionadas, restricciones de red, gestión de secretos, copias de seguridad y protección ante abuso del login; este proyecto no incluye esas operaciones de infraestructura.

Con Maven externo instalado: `mvn clean verify` y `mvn spring-boot:run`. Para demo: `mvn spring-boot:run -Dspring-boot.run.profiles=demo`. Jar: `mvn clean package` y `java -jar target/gaselvolcan-backend-2.0.0.jar` con las variables correctas.

## 3. Modelo de datos

La figura del Anexo 1 propone USUARIO, BOLETA, DETALLE_BOLETA, PRODUCTO y CATEGORIA. Las relaciones implementadas en JPA son Usuario 1:N Boleta, Boleta 1:N DetalleBoleta, Producto 1:N DetalleBoleta y Categoria 1:N Producto. Los roles se guardan como enum en Usuario. La boleta representa la orden de compra académica, no un documento tributario del SII. El servidor calcula subtotales y total con precios en pesos CLP, controla stock y guarda el precio y nombre históricos. Cancela una boleta pendiente/confirmada y repone existencias; no puede cancelar una entregada. Las eliminaciones de usuarios y productos con historial son lógicas para preservar las relaciones. Las boletas solo se eliminan cuando están canceladas. El catálogo público muestra productos activos.

## 4. API y permisos

| Método y ruta (prefijo `/api/v1`) | Acceso |
|---|---|
| POST `/auth/registro` | Público; crea exclusivamente CLIENTE |
| POST `/auth/login` | Público; JWT firmado, 1 hora |
| GET `/auth/me` | Cliente, administrador |
| GET `/tienda/productos`, `/tienda/productos/{id}`, `/tienda/categorias` | Catálogo público |
| POST `/tienda/pedidos` | Cliente, administrador; crea boleta propia |
| GET `/tienda/pedidos`, `/tienda/pedidos/{id}` | Cliente, administrador; solo pedidos propios |
| GET `/productos`, `/productos/{id}` | Administrador, vendedor |
| POST `/productos`, PUT `/productos/{id}`, DELETE `/productos/{id}` | Administrador |
| GET, POST `/categorias`, GET, PUT, DELETE `/categorias/{id}` | Administrador |
| GET, POST `/usuarios`, GET, PUT, DELETE `/usuarios/{id}` | Administrador |
| GET `/boletas`, `/boletas/{id}` | Administrador, vendedor |
| PATCH `/boletas/{id}/estado`, DELETE `/boletas/{id}` | Administrador |

La ruta antigua `GET /api/productos` permanece temporalmente solo para que el catálogo React anterior no se rompa; no permite escrituras y no reemplaza las rutas versionadas. Swagger está en `/swagger-ui/index.html`; OpenAPI JSON `/v3/api-docs`. Las API protegidas requieren `Authorization: Bearer <token>`. La API comprueba el rol y estado vigente del usuario en la base en cada petición. 400 datos inválidos, 401 sin token/credenciales válidos, 403 permisos insuficientes, 404 no encontrado, 409 conflicto de stock, email o relación. 201 creación, 204 eliminación.

Ejemplos de cuerpo JSON:

```json
{"nombre":"Cilindros","descripcion":"Gas doméstico"}
```
```json
{"nombre":"Gas 15 kg","descripcion":"Cilindro doméstico","precio":23500,"stock":20,"categoriaId":1,"activo":true}
```
```json
{"nombre":"Cliente","email":"cliente@ejemplo.com","password":"UnaClavePropia2026!","telefono":"912345678"}
```
```json
{"direccion":"Av. Principal 123","telefono":"912345678","items":[{"productoId":1,"cantidad":2}]}
```

Los IDs son ejemplos; utiliza los que devuelve tu instancia. No se acepta precio, total, usuario o rol enviado por el cliente al crear un pedido/registro.

## 5. Integración React y entrega

`integracion-react/api.js` es un adaptador de fetch con rutas versionadas y token. Se debe integrar en los componentes del frontend real (registro/login, vistas por rol, carrito y pedidos); no basta con cambiar una URL. No se modificó el repositorio frontend anterior. La carpeta incluye una guía. Verifica CORS, elimina fallbacks que simulen una API conectada y confirma cada flujo con MySQL antes de actualizar la documentación. En vendedor muestra solo productos y órdenes, sin botones de edición, usuarios, categorías ni checkout.

## 6. Pruebas y publicación

`ApiIntegrationTest` usa peticiones MockMvc reales con JWT generado por el login; comprueba CRUD, roles, acceso ajeno, stock, cancelación y documentación. El workflow de GitHub Actions prueba H2 y MySQL desechable. Las pruebas todavía no se ejecutaron en el entorno de generación porque no dispone de Maven ni red de dependencias. Revisa errores antes de presentar.

Crea un repositorio nuevo **sin importar ni copiar el historial anterior**, por ejemplo `GasElVolcan-backend-v2`. Inicializa Git en esta carpeta solo después de revisar el código, y publica según las reglas de tu profesor. No se generaron commits fingidos ni se borró el repositorio antiguo. Declara la asistencia de IA si la asignatura lo exige y asegúrate de entender cada componente para responder preguntas.

Consulta `VERIFICACION.md` para distinguir código implementado de resultados realmente comprobados. Actualiza ERS, documentación API, manual y evidencias cuando tengas una ejecución correcta; las capturas anteriores no corresponden a esta versión.
