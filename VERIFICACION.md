# Matriz de revisión — Evaluación 3

Fuente: DSY1104 Evaluación Parcial 3 - Anexo 1 Instrucciones, páginas 1–3 (incluye la Figura 1 de la página 2).

| Exigencia | Evidencia en esta carpeta | Validación pendiente |
|---|---|---|
| Spring Boot y MySQL | pom.xml, application.yml, db/crear-base.sql | Descargar dependencias, iniciar con MySQL |
| Modelo relacional | Usuario, Boleta, DetalleBoleta, Producto, Categoria | Consultar tablas y relaciones en MySQL |
| Repositorios y servicios | Repositorios.java, UsuarioService, CatalogoService, BoletaService | Ejecutar pruebas |
| REST, CRUD, versionado | Controladores /api/v1 | Verificar HTTP y persistencia |
| Swagger | springdoc y OpenAPI bean | Abrir Swagger y autenticar |
| Seguridad y JWT | SecurityConfig, UsuarioService, @PreAuthorize | Verificar token, 401/403 y roles |
| Vendedor solo lectura | ProductoController y BoletaController | Probar y restringir menús React |
| Cliente tienda | TiendaController, BoletaService | Probar creación y acceso a pedidos propios |
| Integración fetch/axios | integracion-react/api.js (adaptador) | Aplicarlo a frontend anterior y probar |
| Pruebas | ApiIntegrationTest y workflow H2/MySQL | Ejecutar; no hay resultados certificados |
| Repositorio y commits | Código independiente, sin .git | Revisar, crear repo, publicar cambios reales |
| Documentación y presentación | README y contratos de API | Actualizar los documentos y capturas de esta versión |

## Prueba manual mínima

1. Ejecutar `mvn clean verify` (o Maven > Lifecycle > verify). No omitir tests.
2. Iniciar MySQL nuevo con variables, comprobar `/actuator/health`, revisar las cinco tablas.
3. Login admin; crear categoría y producto, editar y consultar; registrar cliente, crear vendedor, probar con cada rol.
4. Cliente realiza pedido de dos cilindros; total = 2 × precio de BD; stock disminuye. Otro cliente no puede ver la boleta. Vendedor solo lee productos/boletas. Administrador cancela y stock se restaura. Comprobar rechazo por stock insuficiente y borrados con historial.
5. Revisar Swagger, verificar integración React y CORS. Capturar pruebas reales (sin claves ni JWT).
6. Ejecutar en AWS y repetir pruebas si el docente exige despliegue. El Anexo 1 no exige AWS explícitamente.

No presentar esta matriz como resultados obtenidos. Es una guía de verificación del nuevo código.
