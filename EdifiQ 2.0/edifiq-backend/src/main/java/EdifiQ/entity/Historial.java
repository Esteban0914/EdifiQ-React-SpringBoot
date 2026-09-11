package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="historial")
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_historial")
    private Integer idHistorial;

    @Column(name="accion")
    private String accion;

    @Column(name="tabla_afectada")
    private String tablaAfectada;

    @Column(name="id_registro")
    private Integer idRegistro;

    @Column(name="valor_anterior", columnDefinition="LONGTEXT")
    private String valorAnterior;

    @Column(name="valor_nuevo", columnDefinition="LONGTEXT")
    private String valorNuevo;

    @Column(name="descripcion", columnDefinition="TEXT")
    private String descripcion;

    @Column(name="ip_usuario")
    private String ipUsuario;

    @Column(name="fecha")
    private LocalDateTime fecha;

    @Column(name="id_usuario")
    private Integer idUsuario;

    public Integer getIdHistorial() { return idHistorial; }

    public void setIdHistorial(Integer idHistorial) { this.idHistorial = idHistorial; }

    public String getAccion() { return accion; }

    public void setAccion(String accion) { this.accion = accion; }

    public String getTablaAfectada() { return tablaAfectada; }

    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public Integer getIdRegistro() { return idRegistro; }

    public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }

    public String getValorAnterior() { return valorAnterior; }

    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNuevo() { return valorNuevo; }

    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIpUsuario() { return ipUsuario; }

    public void setIpUsuario(String ipUsuario) { this.ipUsuario = ipUsuario; }

    public LocalDateTime getFecha() { return fecha; }

    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

}