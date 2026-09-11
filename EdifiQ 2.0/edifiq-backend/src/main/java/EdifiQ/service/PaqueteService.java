package EdifiQ.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import EdifiQ.dto.PaqueteRequest;
import EdifiQ.entity.Paquete;
import EdifiQ.repository.ApartamentoPersonaRepository;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.ModuleQueryRepository;
import EdifiQ.repository.PaqueteRepository;
import EdifiQ.repository.PersonaRepository;

@Service
public class PaqueteService {

    private static final String PAQUETE_NO_ENCONTRADO = "Paquete no encontrado";
    private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");

    private final PaqueteRepository repo;
    private final ModuleQueryRepository query;
    private final ApartamentoRepository aptRepo;
    private final PersonaRepository personaRepo;
    private final ApartamentoPersonaRepository relacionRepo;

    public PaqueteService(PaqueteRepository r, ModuleQueryRepository q, ApartamentoRepository a, PersonaRepository p, ApartamentoPersonaRepository relacionRepo) {
        repo = r;
        query = q;
        aptRepo = a;
        personaRepo = p;
        this.relacionRepo = relacionRepo;
    }

    public List<Map<String, Object>> buscar(String q, Integer estado, Integer torre, LocalDate fecha, Integer apartamento) {
        return query.paquetes(q, estado, torre, fecha, apartamento).stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", ((Number) r[0]).intValue());
            m.put("descripcion", r[1]);
            m.put("remitente", r[2]);
            m.put("fechaRecepcion", r[3]);
            m.put("fechaEntrega", r[4]);
            m.put("estado", r[5]);
            m.put("apartamento", r[6]);
            m.put("torre", r[7]);
            m.put("idApartamento", ((Number) r[8]).intValue());
            m.put("idPersona", r[9] == null ? null : ((Number) r[9]).intValue());
            m.put("persona", r[10] == null ? null : r[10] + " " + r[11]);
            m.put("idEstado", ((Number) r[12]).intValue());
            return m;
        }).toList();
    }

    @Transactional
    public void guardar(PaqueteRequest req) {
        Paquete p = req.idPaquete() == null
            ? new Paquete()
            : repo.findById(req.idPaquete())
                .orElseThrow(() -> new IllegalArgumentException(PAQUETE_NO_ENCONTRADO));
        boolean nuevo = req.idPaquete() == null;
        Integer idApartamento = nuevo ? req.idApartamento() : p.getIdApartamento();
        Integer idPersona = nuevo ? req.idPersona() : p.getIdPersona();
        String descripcion = nuevo ? req.descripcion() : p.getDescripcion();
        aptRepo.findById(idApartamento).orElseThrow(() -> new IllegalArgumentException("Apartamento inválido"));
        if (idPersona != null) {
            personaRepo.findById(idPersona).orElseThrow(() -> new IllegalArgumentException("Persona inválida"));
            if (relacionRepo.findActivePeopleByApartment(idApartamento).stream()
                    .noneMatch(x -> ((Number) x[0]).intValue() == idPersona)) {
                throw new IllegalArgumentException("La persona no está asociada al apartamento");
            }
        }
        p.setDescripcion(descripcion.trim());
        p.setRemitente(req.remitente().trim());
        p.setFechaRecepcion(req.fechaRecepcion() == null ? LocalDateTime.now(COLOMBIA_ZONE) : req.fechaRecepcion());
        p.setFechaEntrega(req.fechaEntrega());
        Integer estadoPaquete = req.idEstadoPaquete();
        p.setIdEstadoPaquete(estadoPaquete == null ? 1 : estadoPaquete);
        p.setIdApartamento(idApartamento);
        p.setIdPersona(idPersona);
        repo.save(p);
    }

    @Transactional
    public void entregar(Integer id, Integer idPersona) {
        Paquete p = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(PAQUETE_NO_ENCONTRADO));
        if (idPersona != null) {
            personaRepo.findById(idPersona).orElseThrow(() -> new IllegalArgumentException("Persona inválida"));
            if (relacionRepo.findActivePeopleByApartment(p.getIdApartamento()).stream()
                    .noneMatch(x -> ((Number) x[0]).intValue() == idPersona)) {
                throw new IllegalArgumentException("Solo las personas asociadas al apartamento pueden reclamar el paquete");
            }
        }
        p.setIdPersona(idPersona);
        p.setIdEstadoPaquete(2);
        p.setFechaEntrega(LocalDateTime.now(COLOMBIA_ZONE));
        repo.save(p);
    }

    public List<Map<String, Object>> personasDelPaquete(Integer id) {
        Paquete p = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(PAQUETE_NO_ENCONTRADO));
        return relacionRepo.findActivePeopleByApartment(p.getIdApartamento()).stream().map(x -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", ((Number) x[0]).intValue());
            m.put("nombres", x[1]);
            m.put("apellidos", x[2]);
            m.put("numeroDocumento", x[3]);
            m.put("tipoResidente", x[4]);
            return m;
        }).toList();
    }

    @Transactional
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
