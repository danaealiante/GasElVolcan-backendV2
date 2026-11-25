package cl.gaselvolcan.backend;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
interface UsuarioRepository extends JpaRepository<Usuario,Long> {
 Optional<Usuario> findByEmail(String email);
 boolean existsByEmail(String email);
 long countByRolAndActivoTrue(Rol rol);
}
interface CategoriaRepository extends JpaRepository<Categoria,Long> {
 boolean existsByNombreIgnoreCase(String nombre);
}
interface ProductoRepository extends JpaRepository<Producto,Long> {
 List<Producto> findByActivoTrueOrderByIdAsc();
 boolean existsByCategoriaId(Long id);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Producto p where p.id=:id") Optional<Producto> bloquear(@Param("id") Long id);
}
interface BoletaRepository extends JpaRepository<Boleta,Long> {
 List<Boleta> findByUsuarioIdOrderByIdDesc(Long id);
 List<Boleta> findAllByOrderByIdDesc();
 boolean existsByUsuarioId(Long id);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select b from Boleta b where b.id=:id") Optional<Boleta> bloquear(@Param("id") Long id);
}
interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta,Long> { boolean existsByProductoId(Long id); }
