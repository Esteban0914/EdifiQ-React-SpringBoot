package EdifiQ.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import EdifiQ.entity.Recibo;

public interface ReciboRepository extends JpaRepository<Recibo, Integer> {
	boolean existsByIdTipoServicioAndPeriodoAndIdApartamento(Integer idTipoServicio, String periodo, Integer idApartamento);
	boolean existsByPeriodoAndIdApartamento(String periodo, Integer idApartamento);
}
