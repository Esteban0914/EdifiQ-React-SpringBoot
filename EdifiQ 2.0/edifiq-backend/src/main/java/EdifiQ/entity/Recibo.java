package EdifiQ.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="recibo")
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_recibo")
    private Integer idRecibo;

    @Column(name="id_tipo_servicio")
    private Integer idTipoServicio;

    @Column(name="periodo")
    private String periodo;

    @Column(name="valor")
    private BigDecimal valor;

    @Column(name="fecha_emision")
    private LocalDate fechaEmision;

    @Column(name="fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name="id_estado_recibo")
    private Integer idEstadoRecibo;

    @Column(name="id_apartamento")
    private Integer idApartamento;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Integer getIdRecibo() { return idRecibo; }

    public void setIdRecibo(Integer idRecibo) { this.idRecibo = idRecibo; }

    public Integer getIdTipoServicio() { return idTipoServicio; }

    public void setIdTipoServicio(Integer idTipoServicio) { this.idTipoServicio = idTipoServicio; }

    public String getPeriodo() { return periodo; }

    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getFechaEmision() { return fechaEmision; }

    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Integer getIdEstadoRecibo() { return idEstadoRecibo; }

    public void setIdEstadoRecibo(Integer idEstadoRecibo) { this.idEstadoRecibo = idEstadoRecibo; }

    public Integer getIdApartamento() { return idApartamento; }

    public void setIdApartamento(Integer idApartamento) { this.idApartamento = idApartamento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

}