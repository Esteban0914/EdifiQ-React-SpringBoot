package EdifiQ.repository;

import EdifiQ.entity.ApartamentoPersona;
import EdifiQ.entity.ApartamentoPersonaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ApartamentoPersonaRepository extends JpaRepository<ApartamentoPersona, ApartamentoPersonaId> {
    Optional<ApartamentoPersona> findFirstById_IdPersonaOrderByFechaIngresoDesc(Integer idPersona);

    @Query(value = "SELECT ap.id_persona, p.nombres, p.apellidos, p.numero_documento, tr.nombre, ap.id_tipo_residente, ap.id_apartamento FROM apartamento_persona ap JOIN persona p ON p.id_persona=ap.id_persona JOIN tipo_residente tr ON tr.id_tipo_residente=ap.id_tipo_residente WHERE ap.id_apartamento=:apt AND p.activo=1 AND (ap.fecha_salida IS NULL OR ap.fecha_salida>=CURDATE()) ORDER BY CASE WHEN ap.id_tipo_residente IN (1,2) THEN 0 ELSE 1 END, p.apellidos, p.nombres", nativeQuery=true)
    List<Object[]> findActivePeopleByApartment(@Param("apt") Integer apt);

    @Query(value = "SELECT ap.id_persona, p.nombres, p.apellidos, p.numero_documento, tr.nombre, ap.id_tipo_residente, ap.id_apartamento FROM apartamento_persona ap JOIN persona p ON p.id_persona=ap.id_persona JOIN tipo_residente tr ON tr.id_tipo_residente=ap.id_tipo_residente WHERE ap.id_persona=:persona AND p.activo=1 AND (ap.fecha_salida IS NULL OR ap.fecha_salida>=CURDATE())", nativeQuery=true)
    List<Object[]> findActiveAssociationByPerson(@Param("persona") Integer persona);
}
