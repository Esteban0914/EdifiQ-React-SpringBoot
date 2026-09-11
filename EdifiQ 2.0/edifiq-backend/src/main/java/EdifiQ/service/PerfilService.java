package EdifiQ.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import EdifiQ.dto.CambioPasswordRequest;
import EdifiQ.dto.PerfilRequest;
import EdifiQ.entity.Persona;
import EdifiQ.entity.Usuario;
import EdifiQ.repository.PersonaRepository;
import EdifiQ.repository.UsuarioRepository;

@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilService(UsuarioRepository usuarioRepository, PersonaRepository personaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> obtener(Integer idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Persona p = personaRepository.findById(u.getIdPersona()).orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", u.getIdUsuario());
        m.put("idPersona", p.getIdPersona());
        m.put("username", u.getUsername());
        m.put("numeroDocumento", p.getNumeroDocumento());
        m.put("nombres", p.getNombres());
        m.put("apellidos", p.getApellidos());
        m.put("telefono", p.getTelefono());
        m.put("correo", p.getCorreo());
        m.put("idRol", u.getIdRol());
        return m;
    }

    @Transactional
    public void actualizar(Integer idUsuario, PerfilRequest req) {
        Usuario u = usuarioRepository.findById(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Persona p = personaRepository.findById(u.getIdPersona()).orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
        p.setTelefono(blank(req.telefono()));
        p.setCorreo(blank(req.correo()));
        personaRepository.save(p);
        usuarioRepository.save(u);
    }

    @Transactional
    public void cambiarPassword(Integer idUsuario, CambioPasswordRequest req) {
        Usuario u = usuarioRepository.findById(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!passwordEncoder.matches(req.passwordActual(), u.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        u.setPassword(passwordEncoder.encode(req.nuevaPassword()));
        usuarioRepository.save(u);
    }

    private String blank(String value) {
        return value == null ? null : value.trim();
    }
}
