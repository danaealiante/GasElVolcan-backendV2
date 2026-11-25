package cl.gaselvolcan.backend;

import jakarta.persistence.*;
@Entity @Table(name="categoria",uniqueConstraints=@UniqueConstraint(name="uk_categoria_nombre",columnNames="nombre"))
public class Categoria {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,length=100) String nombre;
 @Column(length=300) String descripcion;
 public Long getId(){return id;} public String getNombre(){return nombre;} public String getDescripcion(){return descripcion;}
 protected Categoria() {}
}
