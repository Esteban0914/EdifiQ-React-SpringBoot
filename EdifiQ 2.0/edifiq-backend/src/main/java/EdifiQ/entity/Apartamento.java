package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="apartamento")
public class Apartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_apartamento")
    private Integer idApartamento;

    @Column(name="numero_apartamento")
    private String numeroApartamento;

    @Column(name="piso")
    private Integer piso;

    @Column(name="activo")
    private Boolean activo;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name="id_torre")
    private Integer idTorre;

    public Integer getIdApartamento() { return idApartamento; }

    public void setIdApartamento(Integer idApartamento) { this.idApartamento = idApartamento; }

    public String getNumeroApartamento() { return numeroApartamento; }

    public void setNumeroApartamento(String numeroApartamento) { this.numeroApartamento = numeroApartamento; }

    public Integer getPiso() { return piso; }

    public void setPiso(Integer piso) { this.piso = piso; }

    public Boolean getActivo() { return activo; }

    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Integer getIdTorre() { return idTorre; }

    public void setIdTorre(Integer idTorre) { this.idTorre = idTorre; }

}