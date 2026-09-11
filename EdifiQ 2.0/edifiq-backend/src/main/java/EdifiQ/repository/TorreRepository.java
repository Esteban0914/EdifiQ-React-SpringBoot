package EdifiQ.repository;

import EdifiQ.entity.Torre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorreRepository extends JpaRepository<Torre, Integer> {
    boolean existsByNombreTorreIgnoreCase(String nombreTorre);
}
