package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tipo_visita")
public class TipoVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tipo_visita")
    private Integer idTipoVisita;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdTipoVisita() { return idTipoVisita; }

    public void setIdTipoVisita(Integer idTipoVisita) { this.idTipoVisita = idTipoVisita; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}