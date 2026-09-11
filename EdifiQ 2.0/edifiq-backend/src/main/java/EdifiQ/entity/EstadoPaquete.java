package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="estado_paquete")
public class EstadoPaquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_estado_paquete")
    private Integer idEstadoPaquete;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdEstadoPaquete() { return idEstadoPaquete; }

    public void setIdEstadoPaquete(Integer idEstadoPaquete) { this.idEstadoPaquete = idEstadoPaquete; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}