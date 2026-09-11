package EdifiQ.service;

import EdifiQ.repository.ModuleQueryRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {
    private static final String VISITAS_HOY = "visitasHoy";
    private static final String RECIBOS_PENDIENTES = "recibosPendientes";
    private final ModuleQueryRepository query;
    @PersistenceContext private EntityManager em;
    public DashboardService(ModuleQueryRepository q){query=q;}
    public Map<String,Object> admin(){Object[] r=query.dashboard();return Map.of("personas",toLong(r[0]),VISITAS_HOY,toLong(r[1]),"paquetesHoy",toLong(r[2]),RECIBOS_PENDIENTES,toLong(r[3]),"reservasHoy",toLong(r[4]));}
    public Map<String,Object> guard(){Map<String,Object> m=new LinkedHashMap<>(admin());m.put("visitasPendientes", count("SELECT COUNT(*) FROM visita WHERE id_estado_visita=1"));m.put("paquetesPendientes",count("SELECT COUNT(*) FROM paquete WHERE id_estado_paquete=1"));return m;}
    public Map<String,Object> resident(Integer idPersona){
        @SuppressWarnings("unchecked")
        java.util.List<Object> apResults = em.createNativeQuery("SELECT id_apartamento FROM apartamento_persona WHERE id_persona=:id AND (fecha_salida IS NULL OR fecha_salida>=CURDATE()) ORDER BY fecha_ingreso DESC")
                .setParameter("id", idPersona)
                .setMaxResults(1)
                .getResultList();
        Object ap = apResults.isEmpty() ? null : apResults.get(0);
        if(ap==null){Map<String,Object> empty=new LinkedHashMap<>();empty.put("apartamento",null);empty.put("paquetes",0);empty.put(VISITAS_HOY,0);empty.put("reservas",0);empty.put(RECIBOS_PENDIENTES,0);return empty;}
        int id=((Number) ap).intValue();
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("apartamento",id);m.put("paquetes",count("SELECT COUNT(*) FROM paquete WHERE id_apartamento="+id+" AND id_estado_paquete=1"));m.put(VISITAS_HOY,count("SELECT COUNT(*) FROM visita WHERE id_apartamento="+id+" AND DATE(fecha_ingreso)=CURDATE()"));m.put("reservas",count("SELECT COUNT(*) FROM reserva_zona WHERE id_apartamento="+id+" AND fecha_reserva>=CURDATE() AND id_estado_reserva<>3"));m.put(RECIBOS_PENDIENTES,count("SELECT COUNT(*) FROM recibo WHERE id_apartamento="+id+" AND id_estado_recibo=1"));return m;
    }
    private long count(String sql){return toLong(em.createNativeQuery(sql).getSingleResult());}
    private long toLong(Object value){return ((Number) value).longValue();}
}