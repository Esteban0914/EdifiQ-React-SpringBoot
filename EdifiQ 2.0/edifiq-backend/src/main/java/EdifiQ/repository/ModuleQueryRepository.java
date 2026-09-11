package EdifiQ.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@SuppressWarnings("unchecked")
public class ModuleQueryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> visitas(String q, Integer estado, Integer tipo, Integer torre, LocalDate fechaDesde, LocalDate fechaHasta, Integer apartamento) {
        String sql = """
            SELECT v.id_visita, v.nombre_visitante, v.documento_visitante,
                   tv.nombre, td.abreviatura, v.motivo_visita, v.fecha_ingreso, v.fecha_salida,
                   ev.nombre, ap.numero_apartamento, t.nombre_torre, v.id_apartamento,
                   v.id_tipo_visita, v.id_tipo_documento, v.id_estado_visita, v.autorizada
            FROM visita v
            JOIN tipo_visita tv ON tv.id_tipo_visita = v.id_tipo_visita
            JOIN tipo_documento td ON td.id_tipo_documento = v.id_tipo_documento
            JOIN estado_visita ev ON ev.id_estado_visita = v.id_estado_visita
            JOIN apartamento ap ON ap.id_apartamento = v.id_apartamento
            JOIN torre t ON t.id_torre = ap.id_torre
            WHERE (:q='' OR v.nombre_visitante LIKE CONCAT('%',:q,'%') OR v.documento_visitante LIKE CONCAT('%',:q,'%') OR v.motivo_visita LIKE CONCAT('%',:q,'%'))
              AND (:estado IS NULL OR v.id_estado_visita=:estado)
              AND (:tipo IS NULL OR v.id_tipo_visita=:tipo)
              AND (:torre IS NULL OR t.id_torre=:torre)
              AND (:fechaDesde IS NULL OR DATE(v.fecha_ingreso) >= :fechaDesde)
              AND (:fechaHasta IS NULL OR DATE(v.fecha_ingreso) <= :fechaHasta)
              AND (:apartamento IS NULL OR v.id_apartamento=:apartamento)
            ORDER BY v.fecha_ingreso DESC
        """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("q", q == null ? "" : q.trim());
        query.setParameter("estado", estado);
        query.setParameter("tipo", tipo);
        query.setParameter("torre", torre);
        query.setParameter("fechaDesde", fechaDesde);
        query.setParameter("fechaHasta", fechaHasta);
        query.setParameter("apartamento", apartamento);
        return query.getResultList();
    }

    public List<Object[]> paquetes(String q, Integer estado, Integer torre, LocalDate fecha, Integer apartamento) {
        String sql = """
            SELECT p.id_paquete, p.descripcion, p.remitente, p.fecha_recepcion, p.fecha_entrega,
                   ep.nombre, ap.numero_apartamento, t.nombre_torre, p.id_apartamento, p.id_persona,
                   pe.nombres, pe.apellidos, p.id_estado_paquete
            FROM paquete p
            JOIN estado_paquete ep ON ep.id_estado_paquete = p.id_estado_paquete
            JOIN apartamento ap ON ap.id_apartamento = p.id_apartamento
            JOIN torre t ON t.id_torre = ap.id_torre
            LEFT JOIN persona pe ON pe.id_persona = p.id_persona
            WHERE (:q='' OR p.descripcion LIKE CONCAT('%',:q,'%') OR p.remitente LIKE CONCAT('%',:q,'%') OR ap.numero_apartamento LIKE CONCAT('%',:q,'%'))
              AND (:estado IS NULL OR p.id_estado_paquete=:estado)
              AND (:torre IS NULL OR t.id_torre=:torre)
              AND (:fecha IS NULL OR DATE(p.fecha_recepcion)=:fecha)
              AND (:apartamento IS NULL OR p.id_apartamento=:apartamento)
            ORDER BY p.fecha_recepcion DESC
        """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("q", q == null ? "" : q.trim());
        query.setParameter("estado", estado);
        query.setParameter("torre", torre);
        query.setParameter("fecha", fecha);
        query.setParameter("apartamento", apartamento);
        return query.getResultList();
    }

    public List<Object[]> recibos(String q, Integer estado, Integer servicio, Integer torre, Integer apartamento) {
        String sql = """
            SELECT r.id_recibo, ts.nombre, r.periodo, r.valor, r.fecha_emision, r.fecha_vencimiento,
                   er.nombre, ap.numero_apartamento, t.nombre_torre, r.id_apartamento, r.id_tipo_servicio, r.id_estado_recibo
            FROM recibo r
            JOIN tipo_servicio ts ON ts.id_tipo_servicio = r.id_tipo_servicio
            JOIN estado_recibo er ON er.id_estado_recibo = r.id_estado_recibo
            JOIN apartamento ap ON ap.id_apartamento = r.id_apartamento
            JOIN torre t ON t.id_torre = ap.id_torre
            WHERE (:q='' OR r.periodo LIKE CONCAT('%',:q,'%') OR ap.numero_apartamento LIKE CONCAT('%',:q,'%'))
              AND (:estado IS NULL OR r.id_estado_recibo=:estado)
              AND (:servicio IS NULL OR r.id_tipo_servicio=:servicio)
              AND (:torre IS NULL OR t.id_torre=:torre)
              AND (:apartamento IS NULL OR r.id_apartamento=:apartamento)
            ORDER BY r.fecha_vencimiento ASC
        """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("q", q == null ? "" : q.trim());
        query.setParameter("estado", estado);
        query.setParameter("servicio", servicio);
        query.setParameter("torre", torre);
        query.setParameter("apartamento", apartamento);
        return query.getResultList();
    }

    public List<Object[]> reservas(String q, Integer estado, Integer zona, Integer torre, LocalDate fecha, Integer apartamento) {
        String sql = """
            SELECT rz.id_reserva, zc.nombre, rz.fecha_reserva, rz.hora_inicio, rz.hora_fin, rz.cantidad_invitados,
                   er.nombre, ap.numero_apartamento, t.nombre_torre, rz.id_zona, rz.id_apartamento, rz.id_estado_reserva
            FROM reserva_zona rz
            JOIN zona_comun zc ON zc.id_zona = rz.id_zona
            JOIN estado_reserva er ON er.id_estado_reserva = rz.id_estado_reserva
            JOIN apartamento ap ON ap.id_apartamento = rz.id_apartamento
            JOIN torre t ON t.id_torre = ap.id_torre
            WHERE (:q='' OR zc.nombre LIKE CONCAT('%',:q,'%') OR ap.numero_apartamento LIKE CONCAT('%',:q,'%'))
              AND (:estado IS NULL OR rz.id_estado_reserva=:estado)
              AND (:zona IS NULL OR rz.id_zona=:zona)
              AND (:torre IS NULL OR t.id_torre=:torre)
              AND (:fecha IS NULL OR rz.fecha_reserva=:fecha)
              AND (:apartamento IS NULL OR rz.id_apartamento=:apartamento)
            ORDER BY rz.fecha_reserva DESC, rz.hora_inicio DESC
        """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("q", q == null ? "" : q.trim());
        query.setParameter("estado", estado);
        query.setParameter("zona", zona);
        query.setParameter("torre", torre);
        query.setParameter("fecha", fecha);
        query.setParameter("apartamento", apartamento);
        return query.getResultList();
    }

    public Object[] dashboard() {
        return (Object[]) entityManager.createNativeQuery("""
            SELECT
              (SELECT COUNT(*) FROM persona WHERE activo=1),
              (SELECT COUNT(*) FROM visita WHERE DATE(fecha_ingreso)=CURDATE()),
              (SELECT COUNT(*) FROM paquete WHERE DATE(fecha_recepcion)=CURDATE()),
              (SELECT COUNT(*) FROM recibo WHERE id_estado_recibo=1),
              (SELECT COUNT(*) FROM reserva_zona WHERE fecha_reserva=CURDATE())
        """).getSingleResult();
    }
}
