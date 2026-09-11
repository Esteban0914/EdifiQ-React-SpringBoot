package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="estado_usuario")
public class EstadoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_estado_usuario")
    private Integer idEstadoUsuario;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdEstadoUsuario() { return idEstadoUsuario; }

    public void setIdEstadoUsuario(Integer idEstadoUsuario) { this.idEstadoUsuario = idEstadoUsuario; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}