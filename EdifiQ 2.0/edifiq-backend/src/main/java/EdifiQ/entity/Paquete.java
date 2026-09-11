package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="paquete")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_paquete")
    private Integer idPaquete;

    @Column(name="descripcion")
    private String descripcion;

    @Column(name="remitente")
    private String remitente;

    @Column(name="fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name="fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name="id_estado_paquete")
    private Integer idEstadoPaquete;

    @Column(name="id_apartamento")
    private Integer idApartamento;

    @Column(name="id_persona")
    private Integer idPersona;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Integer getIdPaquete() { return idPaquete; }

    public void setIdPaquete(Integer idPaquete) { this.idPaquete = idPaquete; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getRemitente() { return remitente; }

    public void setRemitente(String remitente) { this.remitente = remitente; }

    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }

    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public Integer getIdEstadoPaquete() { return idEstadoPaquete; }

    public void setIdEstadoPaquete(Integer idEstadoPaquete) { this.idEstadoPaquete = idEstadoPaquete; }

    public Integer getIdApartamento() { return idApartamento; }

    public void setIdApartamento(Integer idApartamento) { this.idApartamento = idApartamento; }

    public Integer getIdPersona() { return idPersona; }

    public void setIdPersona(Integer idPersona) { this.idPersona = idPersona; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

}