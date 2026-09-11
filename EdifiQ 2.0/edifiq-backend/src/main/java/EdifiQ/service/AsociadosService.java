package EdifiQ.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import EdifiQ.entity.ApartamentoPersona;
import EdifiQ.entity.ApartamentoPersonaId;
import EdifiQ.entity.Persona;
import EdifiQ.repository.ApartamentoPersonaRepository;
import EdifiQ.repository.PersonaRepository;
import EdifiQ.repository.TipoDocumentoRepository;
import EdifiQ.repository.TipoResidenteRepository;

@Service
public class AsociadosService {

    private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");

    private final ApartamentoPersonaRepository rel;
    private final PersonaRepository personas;
    private final TipoDocumentoRepository docs;
    private final TipoResidenteRepository tipos;
    private final ApartmentScopeService scope;

    public AsociadosService(
            ApartamentoPersonaRepository rel,
            PersonaRepository personas,
            TipoDocumentoRepository docs,
            TipoResidenteRepository tipos,
            ApartmentScopeService scope
    ) {
        this.rel = rel;
        this.personas = personas;
        this.docs = docs;
        this.tipos = tipos;
        this.scope = scope;
    }

    public List<Map<String, Object>> listar(Integer apt) {
        return rel.findActivePeopleByApartment(apt).stream()
                .map(this::map)
                .toList();
    }

    public List<Map<String, Object>> listarParaPersona(Integer persona) {
        Integer apt = scope.activeApartmentForPerson(persona);
        return apt == null ? List.of() : listar(apt);
    }

    private Map<String, Object> map(Object[] r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ((Number) r[0]).intValue());
        m.put("nombres", r[1]);
        m.put("apellidos", r[2]);
        m.put("numeroDocumento", r[3]);
        m.put("tipoResidente", r[4]);
        m.put("idTipoResidente", ((Number) r[5]).intValue());
        m.put("idApartamento", ((Number) r[6]).intValue());
        return m;
    }

    @Transactional
    public Map<String, Object> agregar(
            Integer idPersonaPrincipal,
            Integer idTipoDocumento,
            String numeroDocumento,
            String nombres,
            String apellidos,
            String telefono,
            String correo
    ) {
        Integer apt = scope.activeApartmentForPerson(idPersonaPrincipal);
        if (apt == null) {
            throw new IllegalArgumentException("El residente no tiene un apartamento activo");
        }

        docs.findById(idTipoDocumento)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento inválido"));
        tipos.findById(3)
                .orElseThrow(() -> new IllegalArgumentException("Tipo Familiar no configurado"));
        DatosValidationService.validarDocumento(idTipoDocumento, numeroDocumento);
        DatosValidationService.validarTelefono(telefono);

        if (personas.existsByIdTipoDocumentoAndNumeroDocumento(idTipoDocumento, numeroDocumento)) {
            throw new IllegalArgumentException("Ya existe una persona con ese documento");
        }

        Persona p = new Persona();
        p.setIdTipoDocumento(idTipoDocumento);
        p.setNumeroDocumento(numeroDocumento.trim());
        p.setNombres(nombres.trim());
        p.setApellidos(apellidos.trim());
        p.setTelefono(telefono == null ? null : telefono.trim());
        p.setCorreo(correo == null ? null : correo.trim());
        p.setActivo(true);
        p = personas.save(p);

        ApartamentoPersona ap = new ApartamentoPersona();
        ApartamentoPersonaId id = new ApartamentoPersonaId();
        id.setIdApartamento(apt);
        id.setIdPersona(p.getIdPersona());
        ap.setId(id);
        ap.setIdTipoResidente(3);
        ap.setFechaIngreso(LocalDate.now(COLOMBIA_ZONE));
        rel.save(ap);

        return Map.of(
                "id", p.getIdPersona(),
                "nombres", p.getNombres(),
                "apellidos", p.getApellidos(),
                "numeroDocumento", p.getNumeroDocumento(),
                "idApartamento", apt,
                "tipoResidente", "Familiar"
        );
    }

    @Transactional
    public void eliminar(Integer principal, Integer familiar) {
        Integer apt = scope.activeApartmentForPerson(principal);
        if (apt == null) {
            throw new IllegalArgumentException("Apartamento no encontrado");
        }

        ApartamentoPersonaId id = new ApartamentoPersonaId();
        id.setIdApartamento(apt);
        id.setIdPersona(familiar);

        ApartamentoPersona x = rel.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El familiar no pertenece a tu apartamento"));

        if (x.getIdTipoResidente() != 3) {
            throw new IllegalArgumentException("Solo puedes retirar familiares asociados");
        }

        x.setFechaSalida(LocalDate.now(COLOMBIA_ZONE));
        rel.save(x);
    }
}

