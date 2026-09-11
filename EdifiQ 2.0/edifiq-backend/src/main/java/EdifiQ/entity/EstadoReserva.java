package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="estado_reserva")
public class EstadoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_estado_reserva")
    private Integer idEstadoReserva;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdEstadoReserva() { return idEstadoReserva; }

    public void setIdEstadoReserva(Integer idEstadoReserva) { this.idEstadoReserva = idEstadoReserva; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}