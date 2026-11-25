package cl.gaselvolcan.backend;

import jakarta.persistence.*;
@Entity @Table(name="detalle_boleta")
public class DetalleBoleta {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="boleta_id",nullable=false) Boleta boleta;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="producto_id",nullable=false) Producto producto;
 @Column(nullable=false,length=120) String nombreProducto;
 @Column(nullable=false) int cantidad;
 @Column(nullable=false) int precioUnitario;
 @Column(nullable=false) long subtotal;
 protected DetalleBoleta() {}
}
