package EdifiQ.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import EdifiQ.dto.PersonaRequest;
import EdifiQ.dto.PersonaResponse;
import EdifiQ.entity.ApartamentoPersona;
import EdifiQ.entity.ApartamentoPersonaId;
import EdifiQ.entity.Persona;
import EdifiQ.repository.ApartamentoPersonaRepository;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.PersonaQueryRepository;
import EdifiQ.repository.PersonaRepository;
import EdifiQ.repository.TipoDocumentoRepository;
import EdifiQ.repository.TipoResidenteRepository;

@Service
public class PersonaService {

    private static final String PERSONA_NO_ENCONTRADA = "Persona no encontrada";
    private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");

    private final PersonaRepository personaRepository;
    private final PersonaQueryRepository queryRepository;
    private final ApartamentoPersonaRepository apartamentoPersonaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoResidenteRepository tipoResidenteRepository;
    private final ApartamentoRepository apartamentoRepository;

    public PersonaService(
            PersonaRepository personaRepository,
            PersonaQueryRepository queryRepository,
            ApartamentoPersonaRepository apartamentoPersonaRepository,
            TipoDocumentoRepository tipoDocumentoRepository,
            TipoResidenteRepository tipoResidenteRepository,
            ApartamentoRepository apartamentoRepository
    ) {
        this.personaRepository = personaRepository;
        this.queryRepository = queryRepository;
        this.apartamentoPersonaRepository = apartamentoPersonaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoResidenteRepository = tipoResidenteRepository;
        this.apartamentoRepository = apartamentoRepository;
    }

    public List<PersonaResponse> buscar(
            String q,
            Integer tipo,
            Integer estado,
            Integer torre
    ) {
        return queryRepository
                .search(q, tipo, estado, torre)
                .stream()
                .map(this::mapRow)
                .toList();
    }

    private PersonaResponse mapRow(Object[] row) {
        return new PersonaResponse(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                (String) row[7],
                toBoolean(row[8]),
                row[9] == null ? null : ((Number) row[9]).intValue(),
                (String) row[10],
                row[11] == null ? null : ((Number) row[11]).intValue(),
                (String) row[12],
                row[13] == null ? null : ((Number) row[13]).intValue(),
                (String) row[14],
                row[15] == null ? null : toLocalDate(row[15]),
                row[16] == null ? null : toLocalDate(row[16])
        );
    }

    @Transactional
    public PersonaResponse guardar(PersonaRequest req) {
        DatosValidationService.validarTelefono(req.telefono());
        Persona persona = obtenerPersona(req);
        persona.setTelefono(blank(req.telefono()));
        persona.setCorreo(blank(req.correo()));
        persona.setActivo(req.activo() == null || req.activo());
        persona = personaRepository.save(persona);
        final Integer personaId = persona.getIdPersona();
        actualizarRelacionApartamento(req, personaId);
        return buscar(
                persona.getNumeroDocumento(),
                null,
                null,
                null
        ).stream()
                .filter(response -> response.id().equals(personaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "No fue posible recuperar la persona guardada"
                ));
    }

    @Transactional
    public void toggle(Integer id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PERSONA_NO_ENCONTRADA));

        persona.setActivo(!Boolean.TRUE.equals(persona.getActivo()));
        personaRepository.save(persona);
    }

    @Transactional
    public void eliminar(Integer id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PERSONA_NO_ENCONTRADA));

        persona.setActivo(false);
        personaRepository.save(persona);
    }

    public Object stats() {
        long total = personaRepository.count();

        long activos = personaRepository.findAll()
                .stream()
                .filter(persona -> Boolean.TRUE.equals(persona.getActivo()))
                .count();

        List<Map<String, Object>> porTipo = queryRepository.statsByResidentType()
                .stream()
                .map(row -> Map.<String, Object>of(
                "nombre", String.valueOf(row[0]),
                "total", ((Number) row[1]).longValue()
        ))
                .toList();

        return Map.of(
                "total", total,
                "activos", activos,
                "porTipo", porTipo
        );
    }

    private Persona obtenerPersona(PersonaRequest request) {
        if (request.idPersona() != null) {
            return personaRepository.findById(request.idPersona())
                    .orElseThrow(() -> new IllegalArgumentException(PERSONA_NO_ENCONTRADA));
        }

        tipoDocumentoRepository.findById(request.idTipoDocumento())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no válido"));
        DatosValidationService.validarDocumento(request.idTipoDocumento(), request.numeroDocumento());
        if (personaRepository.existsByIdTipoDocumentoAndNumeroDocumento(
                request.idTipoDocumento(), request.numeroDocumento())) {
            throw new IllegalArgumentException("Ya existe una persona con ese documento");
        }

        Persona persona = new Persona();
        persona.setIdTipoDocumento(request.idTipoDocumento());
        persona.setNumeroDocumento(request.numeroDocumento().trim());
        persona.setNombres(request.nombres().trim());
        persona.setApellidos(request.apellidos().trim());
        return persona;
    }

    private void actualizarRelacionApartamento(PersonaRequest request, Integer personaId) {
        if (request.idApartamento() == null || request.idTipoResidente() == null) {
            return;
        }

        apartamentoRepository.findById(request.idApartamento())
                .orElseThrow(() -> new IllegalArgumentException("Apartamento no válido"));
        tipoResidenteRepository.findById(request.idTipoResidente())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de residente no válido"));

        ApartamentoPersona relation = apartamentoPersonaRepository
                .findFirstById_IdPersonaOrderByFechaIngresoDesc(personaId)
                .orElse(null);
        if (relation == null || !request.idApartamento().equals(relation.getId().getIdApartamento())) {
            if (relation != null && relation.getFechaSalida() == null) {
                relation.setFechaSalida(LocalDate.now(COLOMBIA_ZONE));
                apartamentoPersonaRepository.save(relation);
            }
            relation = new ApartamentoPersona();
            ApartamentoPersonaId relationId = new ApartamentoPersonaId();
            relationId.setIdPersona(personaId);
            relationId.setIdApartamento(request.idApartamento());
            relation.setId(relationId);
        }

        relation.setIdTipoResidente(request.idTipoResidente());
        relation.setFechaIngreso(request.fechaIngreso() == null
                ? LocalDate.now(COLOMBIA_ZONE)
                : request.fechaIngreso());
        relation.setFechaSalida(null);
        apartamentoPersonaRepository.save(relation);
    }

    private String blank(String value) {
        return value == null ? null : value.trim();
    }

    /*
     * Las columnas BOOLEAN/TINYINT(1) de MySQL pueden llegar como
     * java.lang.Boolean o como java.lang.Number según el driver y la
     * versión de Hibernate. Este helper soporta ambos casos sin
     * arriesgarse a un ClassCastException.
     */
    private boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof Number n) {
            return n.intValue() == 1;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /*
     * Igual que con los booleanos: columnas DATE pueden llegar como
     * java.sql.Date o directamente como java.time.LocalDate según el
     * driver/versión de Hibernate. Este helper soporta ambos casos.
     */
    private java.time.LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.time.LocalDate ld) {
            return ld;
        }
        if (value instanceof java.sql.Date d) {
            return d.toLocalDate();
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toInstant()
                    .atZone(COLOMBIA_ZONE)
                    .toLocalDate();
        }
        return java.time.LocalDate.parse(value.toString());
    }
}
