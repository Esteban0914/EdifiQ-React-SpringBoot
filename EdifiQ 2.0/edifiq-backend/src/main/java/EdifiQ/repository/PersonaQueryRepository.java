package EdifiQ.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonaQueryRepository extends JpaRepository<EdifiQ.entity.Persona, Integer> {

    @Query(value = """
        SELECT p.id_persona, p.id_tipo_documento, td.nombre, p.numero_documento, p.nombres, p.apellidos,
               p.telefono, p.correo, p.activo, ap.id_apartamento, ap.numero_apartamento,
               t.id_torre, t.nombre_torre, apr.id_tipo_residente, tr.nombre,
               apr.fecha_ingreso, apr.fecha_salida
        FROM persona p
        LEFT JOIN tipo_documento td ON td.id_tipo_documento = p.id_tipo_documento
        LEFT JOIN apartamento_persona apr ON apr.id_persona = p.id_persona
          AND (apr.fecha_salida IS NULL OR apr.fecha_salida >= CURDATE())
        LEFT JOIN apartamento ap ON ap.id_apartamento = apr.id_apartamento
        LEFT JOIN torre t ON t.id_torre = ap.id_torre
        LEFT JOIN tipo_residente tr ON tr.id_tipo_residente = apr.id_tipo_residente
        WHERE (:q = '' OR p.numero_documento LIKE CONCAT('%', :q, '%')
            OR CONCAT(p.nombres, ' ', p.apellidos) LIKE CONCAT('%', :q, '%'))
          AND (:tipo IS NULL OR p.id_tipo_documento = :tipo)
          AND (:estado IS NULL OR p.activo = :estado)
          AND (:torre IS NULL OR t.id_torre = :torre)
        ORDER BY p.apellidos, p.nombres
        """, nativeQuery = true)
    List<Object[]> search(@Param("q") String q,
                          @Param("tipo") Integer tipo,
                          @Param("estado") Integer estado,
                          @Param("torre") Integer torre);

    @Query(value = """
        SELECT tr.nombre, COUNT(*)
        FROM apartamento_persona apr
        JOIN tipo_residente tr ON tr.id_tipo_residente = apr.id_tipo_residente
        GROUP BY tr.nombre
        """, nativeQuery = true)
    List<Object[]> statsByResidentType();
}