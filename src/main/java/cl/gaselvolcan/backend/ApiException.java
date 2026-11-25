package cl.gaselvolcan.backend;

import org.springframework.http.HttpStatus;
public class ApiException extends RuntimeException {
 final HttpStatus status;
 ApiException(HttpStatus status,String message){super(message);this.status=status;}
 static ApiException noExiste(String recurso){return new ApiException(HttpStatus.NOT_FOUND,recurso+" no encontrado");}
 static ApiException conflicto(String mensaje){return new ApiException(HttpStatus.CONFLICT,mensaje);}
 static ApiException invalido(String mensaje){return new ApiException(HttpStatus.BAD_REQUEST,mensaje);}
 static ApiException prohibido(){return new ApiException(HttpStatus.FORBIDDEN,"Acceso no autorizado");}
}
