package EdifiQ.security;

import EdifiQ.entity.Usuario;
import EdifiQ.repository.EstadoUsuarioRepository;
import EdifiQ.repository.PersonaRepository;
import EdifiQ.repository.RolRepository;
import EdifiQ.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final EstadoUsuarioRepository estadoUsuarioRepository;

    public AppUserDetailsService(UsuarioRepository usuarioRepository, PersonaRepository personaRepository,
                                 RolRepository rolRepository, EstadoUsuarioRepository estadoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.estadoUsuarioRepository = estadoUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));
        var persona = personaRepository.findById(usuario.getIdPersona())
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no tiene una persona asociada"));
        var rol = rolRepository.findById(usuario.getIdRol())
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no tiene un rol válido"));
        var estado = estadoUsuarioRepository.findById(usuario.getIdEstadoUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no tiene un estado válido"));

        boolean activo = "Activo".equalsIgnoreCase(estado.getNombre()) && Boolean.TRUE.equals(persona.getActivo());
        return new AppUserPrincipal(usuario.getIdUsuario(), persona.getIdPersona(), usuario.getUsername(),
                usuario.getPassword(), persona.getNombres() + " " + persona.getApellidos(),
                rol.getNombre(), rol.getIdRol(), activo);
    }
}
