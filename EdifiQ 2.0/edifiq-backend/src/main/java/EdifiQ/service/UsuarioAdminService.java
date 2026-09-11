package EdifiQ.service;

import EdifiQ.dto.UsuarioAdminRequest;
import EdifiQ.entity.ApartamentoPersona;
import EdifiQ.entity.ApartamentoPersonaId;
import EdifiQ.entity.EstadoUsuario;
import EdifiQ.entity.Persona;
import EdifiQ.entity.Rol;
import EdifiQ.entity.Usuario;
import EdifiQ.repository.ApartamentoPersonaRepository;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.EstadoUsuarioRepository;
import EdifiQ.repository.PersonaRepository;
import EdifiQ.repository.RolRepository;
import EdifiQ.repository.TipoResidenteRepository;
import EdifiQ.repository.UserAdminQueryRepository;
import EdifiQ.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioAdminService {

    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");
    private final UserAdminQueryRepository query;
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final EstadoUsuarioRepository estadoUsuarioRepository;
    private final TipoResidenteRepository tipoResidenteRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final ApartamentoPersonaRepository apartamentoPersonaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminService(UserAdminQueryRepository query,
                               UsuarioRepository usuarioRepository,
                               PersonaRepository personaRepository,
                               RolRepository rolRepository,
                               EstadoUsuarioRepository estadoUsuarioRepository,
                               TipoResidenteRepository tipoResidenteRepository,
                               ApartamentoRepository apartamentoRepository,
                               ApartamentoPersonaRepository apartamentoPersonaRepository,
                               PasswordEncoder passwordEncoder) {
        this.query = query;
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.estadoUsuarioRepository = estadoUsuarioRepository;
        this.tipoResidenteRepository = tipoResidenteRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.apartamentoPersonaRepository = apartamentoPersonaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Map<String, Object>> buscar(String q, Integer rol, Integer estado) {
        return query.search(q, rol, estado).stream().map(this::map).toList();
    }

    private Map<String, Object> map(Object[] r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", ((Number) r[0]).intValue());
        m.put("idPersona", ((Number) r[1]).intValue());
        m.put("username", r[2]);
        m.put("idRol", ((Number) r[3]).intValue());
        m.put("rol", r[4]);
        m.put("idEstadoUsuario", ((Number) r[5]).intValue());
        m.put("estado", r[6]);
        m.put("idTipoDocumento", ((Number) r[7]).intValue());
        m.put("numeroDocumento", r[8]);
        m.put("nombres", r[9]);
        m.put("apellidos", r[10]);
        m.put("telefono", r[11] == null ? "" : r[11]);
        m.put("correo", r[12] == null ? "" : r[12]);
        m.put("activo", r[13]);
        m.put("idApartamento", r[14] == null ? null : ((Number) r[14]).intValue());
        m.put("apartamento", r[15]);
        m.put("idTorre", r[16] == null ? null : ((Number) r[16]).intValue());
        m.put("torre", r[17]);
        m.put("idTipoResidente", r[18] == null ? null : ((Number) r[18]).intValue());
        m.put("tipoResidente", r[19]);
        return m;
    }

    @Transactional
    public void guardar(UsuarioAdminRequest req) {
        Persona persona = obtenerPersona(req);
        var rol = rolRepository.findById(req.idRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol inválido"));
        Integer estadoId = req.idEstadoUsuario();
        var estado = estadoUsuarioRepository.findById(estadoId == null ? 1 : estadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado de usuario inválido"));
        Usuario usuario = obtenerUsuario(req);

        validarDisponibilidad(req, persona);
        actualizarUsuario(usuario, persona, rol, estado, req);
        usuarioRepository.save(usuario);
        actualizarRelacion(req, persona.getIdPersona());
    }

    @Transactional
    public void toggle(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO));
        usuario.setIdEstadoUsuario(usuario.getIdEstadoUsuario() == 1 ? 2 : 1);
        usuarioRepository.save(usuario);

        Persona persona = personaRepository.findById(usuario.getIdPersona()).orElse(null);
        if (persona != null) {
            persona.setActivo(usuario.getIdEstadoUsuario() == 1);
            personaRepository.save(persona);
        }
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO));
        usuario.setIdEstadoUsuario(2);
        usuarioRepository.save(usuario);

        Persona persona = personaRepository.findById(usuario.getIdPersona()).orElse(null);
        if (persona != null) {
            persona.setActivo(false);
            personaRepository.save(persona);
        }
    }

    private Persona obtenerPersona(UsuarioAdminRequest request) {
        if (request.idPersona() == null) {
            throw new IllegalArgumentException("Selecciona una persona existente para crear el usuario");
        }
        Persona persona = personaRepository.findById(request.idPersona())
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
        DatosValidationService.validarDocumento(persona.getIdTipoDocumento(), persona.getNumeroDocumento());
        DatosValidationService.validarTelefono(persona.getTelefono());
        return persona;
    }

    private Usuario obtenerUsuario(UsuarioAdminRequest request) {
        if (request.idUsuario() == null) {
            return new Usuario();
        }
        return usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO));
    }

    private void validarDisponibilidad(UsuarioAdminRequest request, Persona persona) {
        usuarioRepository.findByUsername(request.username().trim()).ifPresent(existing -> {
            if (!existing.getIdUsuario().equals(request.idUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario ya está registrado");
            }
        });
        usuarioRepository.findByIdPersona(persona.getIdPersona()).ifPresent(existing -> {
            if (request.idUsuario() == null || !existing.getIdUsuario().equals(request.idUsuario())) {
                throw new IllegalArgumentException("Esta persona ya tiene una cuenta de usuario ("
                        + existing.getUsername() + ")");
            }
        });
    }

    private void actualizarUsuario(Usuario usuario, Persona persona, Rol rol, EstadoUsuario estado,
                                   UsuarioAdminRequest request) {
        usuario.setUsername(request.username().trim());
        usuario.setIdPersona(persona.getIdPersona());
        usuario.setIdRol(rol.getIdRol());
        usuario.setIdEstadoUsuario(estado.getIdEstadoUsuario());
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        } else if (request.idUsuario() == null) {
            throw new IllegalArgumentException("La contraseña es obligatoria al crear un usuario");
        }
    }

    private void actualizarRelacion(UsuarioAdminRequest request, Integer personaId) {
        if (request.idApartamento() == null || request.idTipoResidente() == null) {
            return;
        }
        apartamentoRepository.findById(request.idApartamento())
                .orElseThrow(() -> new IllegalArgumentException("Apartamento inválido"));
        tipoResidenteRepository.findById(request.idTipoResidente())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de residente inválido"));
        ApartamentoPersona relacion = apartamentoPersonaRepository
                .findFirstById_IdPersonaOrderByFechaIngresoDesc(personaId)
                .orElse(null);
        if (relacion == null || !request.idApartamento().equals(relacion.getId().getIdApartamento())) {
            if (relacion != null && relacion.getFechaSalida() == null) {
                relacion.setFechaSalida(LocalDate.now(COLOMBIA_ZONE));
                apartamentoPersonaRepository.save(relacion);
            }
            relacion = new ApartamentoPersona();
            var id = new ApartamentoPersonaId();
            id.setIdPersona(personaId);
            id.setIdApartamento(request.idApartamento());
            relacion.setId(id);
        }
        relacion.setIdTipoResidente(request.idTipoResidente());
        relacion.setFechaIngreso(request.fechaIngreso() == null
                ? LocalDate.now(COLOMBIA_ZONE) : request.fechaIngreso());
        relacion.setFechaSalida(null);
        apartamentoPersonaRepository.save(relacion);
    }
}
