package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tipo_documento")
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tipo_documento")
    private Integer idTipoDocumento;

    @Column(name="nombre")
    private String nombre;

    @Column(name="abreviatura")
    private String abreviatura;

    public Integer getIdTipoDocumento() { return idTipoDocumento; }

    public void setIdTipoDocumento(Integer idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getAbreviatura() { return abreviatura; }

    public void setAbreviatura(String abreviatura) { this.abreviatura = abreviatura; }

}