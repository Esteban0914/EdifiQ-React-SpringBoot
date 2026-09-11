package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="reserva_zona")
public class ReservaZona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_reserva")
    private Integer idReserva;

    @Column(name="fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name="hora_inicio")
    private LocalTime horaInicio;

    @Column(name="hora_fin")
    private LocalTime horaFin;

    @Column(name="cantidad_invitados")
    private Integer cantidadInvitados;

    @Column(name="id_estado_reserva")
    private Integer idEstadoReserva;

    @Column(name="id_zona")
    private Integer idZona;

    @Column(name="id_apartamento")
    private Integer idApartamento;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Integer getIdReserva() { return idReserva; }

    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public LocalDate getFechaReserva() { return fechaReserva; }

    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalTime getHoraInicio() { return horaInicio; }

    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }

    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public Integer getCantidadInvitados() { return cantidadInvitados; }

    public void setCantidadInvitados(Integer cantidadInvitados) { this.cantidadInvitados = cantidadInvitados; }

    public Integer getIdEstadoReserva() { return idEstadoReserva; }

    public void setIdEstadoReserva(Integer idEstadoReserva) { this.idEstadoReserva = idEstadoReserva; }

    public Integer getIdZona() { return idZona; }

    public void setIdZona(Integer idZona) { this.idZona = idZona; }

    public Integer getIdApartamento() { return idApartamento; }

    public void setIdApartamento(Integer idApartamento) { this.idApartamento = idApartamento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

}