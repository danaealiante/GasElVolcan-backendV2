package cl.gaselvolcan.backend;

import jakarta.persistence.*;
@Entity @Table(name="usuario", uniqueConstraints=@UniqueConstraint(name="uk_usuario_email",columnNames="email"))
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,length=100) String nombre;
 @Column(nullable=false,length=180) String email;
 @Column(nullable=false,length=100) String passwordHash;
 @Column(length=15) String telefono;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) Rol rol;
 @Column(nullable=false) boolean activo=true;
 public Long getId(){return id;} public String getNombre(){return nombre;}
 protected Usuario() {}
}
