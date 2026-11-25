package cl.gaselvolcan.backend;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="boleta")
public class Boleta {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) Usuario usuario;
 @Column(nullable=false) Instant fecha;
 @Column(nullable=false,length=250) String direccion;
 @Column(nullable=false,length=15) String telefono;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) EstadoBoleta estado;
 @Column(nullable=false) long total;
 @OneToMany(mappedBy="boleta",cascade=CascadeType.ALL,orphanRemoval=true) List<DetalleBoleta> detalles=new ArrayList<>();
 @Version Long version;
 protected Boleta() {}
}
