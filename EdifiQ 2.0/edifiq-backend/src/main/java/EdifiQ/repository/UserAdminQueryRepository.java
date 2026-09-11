package EdifiQ.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class UserAdminQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> search(String q, Integer rol, Integer estado) {
        String sql = """
            SELECT u.id_usuario, u.id_persona, u.username, u.id_rol, r.nombre,
                   u.id_estado_usuario, eu.nombre,
                   p.id_tipo_documento, p.numero_documento, p.nombres, p.apellidos, p.telefono, p.correo,
                   p.activo, ap.id_apartamento, ap.numero_apartamento, t.id_torre, t.nombre_torre,
                   apr.id_tipo_residente, tr.nombre
            FROM usuario u
            JOIN persona p ON p.id_persona = u.id_persona
            JOIN rol r ON r.id_rol = u.id_rol
            JOIN estado_usuario eu ON eu.id_estado_usuario = u.id_estado_usuario
            LEFT JOIN apartamento_persona apr
              ON apr.id_persona = p.id_persona
             AND (apr.fecha_salida IS NULL OR apr.fecha_salida >= CURDATE())
            LEFT JOIN apartamento ap ON ap.id_apartamento = apr.id_apartamento
            LEFT JOIN torre t ON t.id_torre = ap.id_torre
            LEFT JOIN tipo_residente tr ON tr.id_tipo_residente = apr.id_tipo_residente
            WHERE (:q = '' OR u.username LIKE CONCAT('%',:q,'%')
                   OR p.numero_documento LIKE CONCAT('%',:q,'%')
                   OR CONCAT(p.nombres,' ',p.apellidos) LIKE CONCAT('%',:q,'%'))
              AND (:rol IS NULL OR u.id_rol = :rol)
              AND (:estado IS NULL OR u.id_estado_usuario = :estado)
            ORDER BY r.nombre, p.apellidos, p.nombres
        """;
        var query = entityManager.createNativeQuery(sql);
        query.setParameter("q", q == null ? "" : q.trim());
        query.setParameter("rol", rol);
        query.setParameter("estado", estado);
        return query.getResultList();
    }
}
