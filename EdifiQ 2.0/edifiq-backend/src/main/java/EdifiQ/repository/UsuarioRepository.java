package EdifiQ.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import EdifiQ.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByIdPersona(Integer idPersona);

    @Query(value = "SELECT u.* FROM usuario u JOIN apartamento_persona ap ON ap.id_persona = u.id_persona WHERE ap.id_apartamento = :apartamento AND u.id_estado_usuario = 1 AND (ap.fecha_salida IS NULL OR ap.fecha_salida >= CURDATE())", nativeQuery = true)
    List<Usuario> findActivosByApartamento(Integer apartamento);

}