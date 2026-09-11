package EdifiQ.entity;

import jakarta.persistence.*;

@Entity
@Table(name="estado_recibo")
public class EstadoRecibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_estado_recibo")
    private Integer idEstadoRecibo;

    @Column(name="nombre")
    private String nombre;

    public Integer getIdEstadoRecibo() { return idEstadoRecibo; }

    public void setIdEstadoRecibo(Integer idEstadoRecibo) { this.idEstadoRecibo = idEstadoRecibo; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

}