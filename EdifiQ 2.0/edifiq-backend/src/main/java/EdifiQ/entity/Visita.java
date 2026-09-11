package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="visita")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_visita")
    private Integer idVisita;

    @Column(name="id_tipo_visita")
    private Integer idTipoVisita;

    @Column(name="id_tipo_documento")
    private Integer idTipoDocumento;

    @Column(name="nombre_visitante")
    private String nombreVisitante;

    @Column(name="documento_visitante")
    private String documentoVisitante;

    @Column(name="motivo_visita")
    private String motivoVisita;

    @Column(name="fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @Column(name="fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name="id_estado_visita")
    private Integer idEstadoVisita;

    @Column(name="autorizada")
    private Boolean autorizada;

    @Column(name="id_apartamento")
    private Integer idApartamento;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Integer getIdVisita() { return idVisita; }

    public void setIdVisita(Integer idVisita) { this.idVisita = idVisita; }

    public Integer getIdTipoVisita() { return idTipoVisita; }

    public void setIdTipoVisita(Integer idTipoVisita) { this.idTipoVisita = idTipoVisita; }

    public Integer getIdTipoDocumento() { return idTipoDocumento; }

    public void setIdTipoDocumento(Integer idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNombreVisitante() { return nombreVisitante; }

    public void setNombreVisitante(String nombreVisitante) { this.nombreVisitante = nombreVisitante; }

    public String getDocumentoVisitante() { return documentoVisitante; }

    public void setDocumentoVisitante(String documentoVisitante) { this.documentoVisitante = documentoVisitante; }

    public String getMotivoVisita() { return motivoVisita; }

    public void setMotivoVisita(String motivoVisita) { this.motivoVisita = motivoVisita; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }

    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDateTime getFechaSalida() { return fechaSalida; }

    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }

    public Integer getIdEstadoVisita() { return idEstadoVisita; }

    public void setIdEstadoVisita(Integer idEstadoVisita) { this.idEstadoVisita = idEstadoVisita; }

    public Boolean getAutorizada() { return autorizada; }

    public void setAutorizada(Boolean autorizada) { this.autorizada = autorizada; }

    public Integer getIdApartamento() { return idApartamento; }

    public void setIdApartamento(Integer idApartamento) { this.idApartamento = idApartamento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

}