package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="zona_comun")
public class ZonaComun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_zona")
    private Integer idZona;

    @Column(name="nombre")
    private String nombre;

    @Column(name="descripcion")
    private String descripcion;

    public Integer getIdZona() { return idZona; }

    public void setIdZona(Integer idZona) { this.idZona = idZona; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}