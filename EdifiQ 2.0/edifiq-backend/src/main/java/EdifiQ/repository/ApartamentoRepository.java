package EdifiQ.repository;

import EdifiQ.entity.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartamentoRepository extends JpaRepository<Apartamento, Integer> {
}
