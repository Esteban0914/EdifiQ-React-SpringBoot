package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="torre")
public class Torre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_torre")
    private Integer idTorre;

    @Column(name="nombre_torre")
    private String nombreTorre;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Integer getIdTorre() { return idTorre; }

    public void setIdTorre(Integer idTorre) { this.idTorre = idTorre; }

    public String getNombreTorre() { return nombreTorre; }

    public void setNombreTorre(String nombreTorre) { this.nombreTorre = nombreTorre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

}