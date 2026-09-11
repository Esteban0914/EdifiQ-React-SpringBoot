package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_notificacion")
    private Integer idNotificacion;

    @Column(name="titulo")
    private String titulo;

    @Column(name="mensaje", columnDefinition="TEXT")
    private String mensaje;

    @Column(name="leida")
    private Boolean leida;

    @Column(name="fecha")
    private LocalDateTime fecha;

    @Column(name="id_usuario")
    private Integer idUsuario;

    public Integer getIdNotificacion() { return idNotificacion; }

    public void setIdNotificacion(Integer idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getLeida() { return leida; }

    public void setLeida(Boolean leida) { this.leida = leida; }

    public LocalDateTime getFecha() { return fecha; }

    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

}