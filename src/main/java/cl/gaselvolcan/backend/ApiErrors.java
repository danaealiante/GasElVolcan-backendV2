package cl.gaselvolcan.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(ApiException.class) ResponseEntity<?> api(ApiException e){return ResponseEntity.status(e.status).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validacion(MethodArgumentNotValidException e){Map<String,String> campos=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(f->campos.putIfAbsent(f.getField(),String.valueOf(f.getDefaultMessage()))); return ResponseEntity.badRequest().body(Map.of("mensaje","Datos inválidos","campos",campos));}
 @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<?> json(){return ResponseEntity.badRequest().body(Map.of("mensaje","JSON inválido o valor no admitido"));}
 @ExceptionHandler({DataIntegrityViolationException.class,ObjectOptimisticLockingFailureException.class}) ResponseEntity<?> conflicto(){return ResponseEntity.status(409).body(Map.of("mensaje","Operación en conflicto con los datos existentes; reintenta o revisa las relaciones"));}
 @ExceptionHandler(AccessDeniedException.class) ResponseEntity<?> denegado(){return ResponseEntity.status(403).body(Map.of("mensaje","Acceso no autorizado"));}
}
