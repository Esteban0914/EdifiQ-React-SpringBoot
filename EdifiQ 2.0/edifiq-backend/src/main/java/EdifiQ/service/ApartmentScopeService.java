package EdifiQ.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class ApartmentScopeService {

    private static final String ACTIVE_APARTMENT_QUERY = """
            SELECT id_apartamento
            FROM apartamento_persona
            WHERE id_persona = :id
              AND (fecha_salida IS NULL OR fecha_salida >= CURDATE())
            ORDER BY fecha_ingreso DESC
            """;

    @PersistenceContext
    private EntityManager entityManager;

    public Integer activeApartmentForPerson(Integer idPersona) {
        List<?> results = entityManager.createNativeQuery(ACTIVE_APARTMENT_QUERY)
                .setParameter("id", idPersona)
                .setMaxResults(1)
                .getResultList();

        if (results.isEmpty() || results.get(0) == null) {
            return null;
        }

        return ((Number) results.get(0)).intValue();
    }
}