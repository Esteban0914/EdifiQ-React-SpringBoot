package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tipo_servicio")
public class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tipo_servicio")
    private Integer idTipoServicio;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdTipoServicio() { return idTipoServicio; }

    public void setIdTipoServicio(Integer idTipoServicio) { this.idTipoServicio = idTipoServicio; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}