package EdifiQ.service;

import java.util.List;
import java.util.Map;

import EdifiQ.dto.VigilanteRequest;
import EdifiQ.entity.Persona;
import EdifiQ.entity.Usuario;
import EdifiQ.repository.PersonaRepository;
import EdifiQ.repository.RolRepository;
import EdifiQ.repository.UserQueryRepository;
import EdifiQ.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VigilanteService {

   private static final String VIGILANTE_NO_ENCONTRADO = "Vigilante no encontrado";

   private final UserQueryRepository query;
   private final UsuarioRepository userRepository;
   private final PersonaRepository personaRepository;
   private final RolRepository rolRepository;
   private final PasswordEncoder passwordEncoder;

   public VigilanteService(UserQueryRepository query, UsuarioRepository userRepository,
                     PersonaRepository personaRepository, RolRepository rolRepository,
                     PasswordEncoder passwordEncoder) {
      this.query = query;
      this.userRepository = userRepository;
      this.personaRepository = personaRepository;
      this.rolRepository = rolRepository;
      this.passwordEncoder = passwordEncoder;
   }

   public List<Map<String, Object>> buscar(String search, Integer estado) {
      return query.vigilantes(search, estado).stream().map(row -> Map.of(
            "idUsuario", ((Number) row[0]).intValue(),
            "idPersona", ((Number) row[1]).intValue(),
            "nombres", row[2],
            "apellidos", row[3],
            "numeroDocumento", row[4],
            "telefono", row[5] == null ? "" : row[5],
            "correo", row[6] == null ? "" : row[6],
            "username", row[7],
            "estado", row[8],
            "idEstado", ((Number) row[9]).intValue()
      )).toList();
   }

   @Transactional
   public void guardar(VigilanteRequest request) {
      var rol = rolRepository.findAll().stream()
            .filter(item -> "Vigilante".equalsIgnoreCase(item.getNombre()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Rol Vigilante no configurado"));

      Persona persona = request.idPersona() == null
            ? new Persona()
            : personaRepository.findById(request.idPersona())
                  .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
      persona.setIdTipoDocumento(1);
      persona.setNumeroDocumento(request.numeroDocumento().trim());
      persona.setNombres(request.nombres().trim());
      persona.setApellidos(request.apellidos().trim());
      persona.setTelefono(request.telefono());
      persona.setCorreo(request.correo());
      persona.setActivo(request.activo() == null || request.activo());
      persona = personaRepository.save(persona);

      Usuario usuario = request.idUsuario() == null
            ? new Usuario()
            : userRepository.findById(request.idUsuario())
                  .orElseThrow(() -> new IllegalArgumentException(VIGILANTE_NO_ENCONTRADO));
      usuario.setIdPersona(persona.getIdPersona());
      usuario.setIdRol(rol.getIdRol());
      usuario.setIdEstadoUsuario(request.activo() == null || request.activo() ? 1 : 2);
      usuario.setUsername(request.username().trim());
      if (request.password() != null && !request.password().isBlank()) {
         usuario.setPassword(passwordEncoder.encode(request.password()));
      } else if (request.idUsuario() == null) {
         throw new IllegalArgumentException("La contraseña es obligatoria al crear un vigilante");
      }
      userRepository.save(usuario);
   }

   @Transactional
   public void toggle(Integer id) {
      Usuario usuario = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(VIGILANTE_NO_ENCONTRADO));
      usuario.setIdEstadoUsuario(usuario.getIdEstadoUsuario() == 1 ? 2 : 1);
      userRepository.save(usuario);

      Persona persona = personaRepository.findById(usuario.getIdPersona()).orElse(null);
      if (persona != null) {
         persona.setActivo(usuario.getIdEstadoUsuario() == 1);
         personaRepository.save(persona);
      }
   }

   @Transactional
   public void eliminar(Integer id) {
      Usuario usuario = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(VIGILANTE_NO_ENCONTRADO));
      usuario.setIdEstadoUsuario(2);
      userRepository.save(usuario);
   }
}
