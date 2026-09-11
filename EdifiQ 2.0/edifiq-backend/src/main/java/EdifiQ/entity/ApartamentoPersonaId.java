package EdifiQ.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ApartamentoPersonaId implements Serializable {
    private Integer idApartamento;
    private Integer idPersona;
    public Integer getIdApartamento(){return idApartamento;} public void setIdApartamento(Integer v){idApartamento=v;}
    public Integer getIdPersona(){return idPersona;} public void setIdPersona(Integer v){idPersona=v;}
    @Override public boolean equals(Object o){if(this==o)return true;if(!(o instanceof ApartamentoPersonaId x))return false;return Objects.equals(idApartamento,x.idApartamento)&&Objects.equals(idPersona,x.idPersona);}
    @Override public int hashCode(){return Objects.hash(idApartamento,idPersona);}
}
