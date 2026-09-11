package EdifiQ.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="apartamento_persona")
public class ApartamentoPersona {
    @EmbeddedId private ApartamentoPersonaId id;
    @Column(name="id_tipo_residente") private Integer idTipoResidente;
    @Column(name="fecha_ingreso") private LocalDate fechaIngreso;
    @Column(name="fecha_salida") private LocalDate fechaSalida;
    public ApartamentoPersonaId getId(){return id;} public void setId(ApartamentoPersonaId v){id=v;}
    public Integer getIdTipoResidente(){return idTipoResidente;} public void setIdTipoResidente(Integer v){idTipoResidente=v;}
    public LocalDate getFechaIngreso(){return fechaIngreso;} public void setFechaIngreso(LocalDate v){fechaIngreso=v;}
    public LocalDate getFechaSalida(){return fechaSalida;} public void setFechaSalida(LocalDate v){fechaSalida=v;}
}
