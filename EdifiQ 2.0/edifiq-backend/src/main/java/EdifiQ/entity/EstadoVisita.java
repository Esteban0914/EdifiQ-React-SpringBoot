package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="estado_visita")
public class EstadoVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_estado_visita")
    private Integer idEstadoVisita;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdEstadoVisita() { return idEstadoVisita; }

    public void setIdEstadoVisita(Integer idEstadoVisita) { this.idEstadoVisita = idEstadoVisita; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}