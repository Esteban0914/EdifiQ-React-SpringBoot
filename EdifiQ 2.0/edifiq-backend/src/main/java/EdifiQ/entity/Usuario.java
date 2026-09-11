package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Integer idUsuario;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="id_estado_usuario")
    private Integer idEstadoUsuario;

    @Column(name="id_persona")
    private Integer idPersona;

    @Column(name="id_rol")
    private Integer idRol;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Integer getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Integer getIdEstadoUsuario() { return idEstadoUsuario; }

    public void setIdEstadoUsuario(Integer idEstadoUsuario) { this.idEstadoUsuario = idEstadoUsuario; }

    public Integer getIdPersona() { return idPersona; }

    public void setIdPersona(Integer idPersona) { this.idPersona = idPersona; }

    public Integer getIdRol() { return idRol; }

    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

}