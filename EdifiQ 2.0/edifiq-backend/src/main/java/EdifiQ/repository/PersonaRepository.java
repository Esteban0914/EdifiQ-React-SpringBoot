package EdifiQ.repository;

import EdifiQ.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    boolean existsByIdTipoDocumentoAndNumeroDocumento(Integer idTipoDocumento, String numeroDocumento);

    Optional<Persona> findByIdTipoDocumentoAndNumeroDocumento(Integer idTipoDocumento, String numeroDocumento);
}
