package cl.gaselvolcan.backend;

import jakarta.persistence.*;
@Entity @Table(name="producto")
public class Producto {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,length=120) String nombre;
 @Column(length=400) String descripcion;
 @Column(nullable=false) int precio;
 @Column(nullable=false) int stock;
 @Column(nullable=false) boolean activo=true;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="categoria_id",nullable=false) Categoria categoria;
 @Version Long version;
 public Long getId(){return id;} public int getStock(){return stock;} public void setStock(int stock){this.stock=stock;} public boolean isActivo(){return activo;} public String getNombre(){return nombre;} public int getPrecio(){return precio;} public String getDescripcion(){return descripcion;} public Categoria getCategoria(){return categoria;} public void setActivo(boolean activo){this.activo=activo;} public void actualizar(String n,String d,int precio,int stock,boolean activo,Categoria categoria){this.nombre=n;this.descripcion=d;this.precio=precio;this.stock=stock;this.activo=activo;this.categoria=categoria;}
 protected Producto() {}
}
