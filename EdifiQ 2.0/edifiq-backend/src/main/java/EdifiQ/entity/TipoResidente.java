package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tipo_residente")
public class TipoResidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tipo_residente")
    private Integer idTipoResidente;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdTipoResidente() { return idTipoResidente; }

    public void setIdTipoResidente(Integer idTipoResidente) { this.idTipoResidente = idTipoResidente; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}