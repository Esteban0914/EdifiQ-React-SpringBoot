package EdifiQ.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import EdifiQ.dto.ReciboMasivoRequest;
import EdifiQ.dto.ReciboRequest;
import EdifiQ.entity.Apartamento;
import EdifiQ.entity.Notificacion;
import EdifiQ.entity.Recibo;
import EdifiQ.entity.TipoServicio;
import EdifiQ.repository.ApartamentoRepository;
import EdifiQ.repository.ModuleQueryRepository;
import EdifiQ.repository.NotificacionRepository;
import EdifiQ.repository.ReciboRepository;
import EdifiQ.repository.TipoServicioRepository;
import EdifiQ.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReciboService {

	private static final String RECIBO_NO_ENCONTRADO = "Recibo no encontrado";
	private static final String SERVICIO_INVALIDO = "Servicio inválido";
	private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");

	private final ReciboRepository repository;
	private final ModuleQueryRepository query;
	private final TipoServicioRepository servicioRepository;
	private final ApartamentoRepository apartamentoRepository;
	private final UsuarioRepository usuarioRepository;
	private final NotificacionRepository notificacionRepository;

	public ReciboService(
			ReciboRepository repository,
			ModuleQueryRepository query,
			TipoServicioRepository servicioRepository,
			ApartamentoRepository apartamentoRepository,
			UsuarioRepository usuarioRepository,
			NotificacionRepository notificacionRepository
	) {
		this.repository = repository;
		this.query = query;
		this.servicioRepository = servicioRepository;
		this.apartamentoRepository = apartamentoRepository;
		this.usuarioRepository = usuarioRepository;
		this.notificacionRepository = notificacionRepository;
	}

	public List<Map<String, Object>> buscar(String search, Integer estado, Integer servicio, Integer torre, Integer apartamento) {
		return query.recibos(search, estado, servicio, torre, apartamento).stream().map(row -> {
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("id", ((Number) row[0]).intValue());
			result.put("servicio", row[1]);
			result.put("periodo", row[2]);
			result.put("valor", row[3]);
			result.put("fechaEmision", row[4]);
			result.put("fechaVencimiento", row[5]);
			result.put("estado", row[6]);
			result.put("apartamento", row[7]);
			result.put("torre", row[8]);
			result.put("idApartamento", ((Number) row[9]).intValue());
			result.put("idTipoServicio", ((Number) row[10]).intValue());
			result.put("idEstado", ((Number) row[11]).intValue());
			return result;
		}).toList();
	}

	@Transactional
	public void guardar(ReciboRequest request) {
		servicioRepository.findById(request.idTipoServicio())
				.orElseThrow(() -> new IllegalArgumentException(SERVICIO_INVALIDO));
		apartamentoRepository.findById(request.idApartamento())
				.orElseThrow(() -> new IllegalArgumentException("Apartamento inválido"));

		Recibo recibo = request.idRecibo() == null
				? new Recibo()
				: repository.findById(request.idRecibo())
						.orElseThrow(() -> new IllegalArgumentException(RECIBO_NO_ENCONTRADO));
		Integer estado = request.idEstadoRecibo();
		recibo.setIdTipoServicio(request.idTipoServicio());
		recibo.setPeriodo(request.periodo().trim());
		recibo.setValor(request.valor());
		recibo.setFechaEmision(request.fechaEmision());
		recibo.setFechaVencimiento(request.fechaVencimiento());
		recibo.setIdEstadoRecibo(estado == null ? 1 : estado);
		recibo.setIdApartamento(request.idApartamento());
		repository.save(recibo);
	}

	@Transactional
	public int guardarPorTorre(ReciboMasivoRequest request) {
		servicioRepository.findById(request.idTipoServicio())
				.orElseThrow(() -> new IllegalArgumentException(SERVICIO_INVALIDO));
		String periodo = request.periodo().trim();
		List<Apartamento> apartamentos = apartamentoRepository.findAll().stream()
				.filter(apartment -> Boolean.TRUE.equals(apartment.getActivo())
						&& request.idTorre().equals(apartment.getIdTorre()))
				.toList();
		if (apartamentos.isEmpty()) {
			throw new IllegalArgumentException("La torre no tiene apartamentos activos");
		}

		Integer estado = request.idEstadoRecibo();
		int creados = 0;
		for (Apartamento apartamento : apartamentos) {
			if (!repository.existsByPeriodoAndIdApartamento(periodo, apartamento.getIdApartamento())) {
				Recibo recibo = new Recibo();
				recibo.setIdTipoServicio(request.idTipoServicio());
				recibo.setPeriodo(periodo);
				recibo.setValor(request.valor());
				recibo.setFechaEmision(request.fechaEmision());
				recibo.setFechaVencimiento(request.fechaVencimiento());
				recibo.setIdEstadoRecibo(estado == null ? 1 : estado);
				recibo.setIdApartamento(apartamento.getIdApartamento());
				repository.save(recibo);
				creados++;
			}
		}
		return creados;
	}

	@Transactional
	public void pagar(Integer id, Integer apartamento) {
		Recibo recibo = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(RECIBO_NO_ENCONTRADO));
		if (apartamento != null && !apartamento.equals(recibo.getIdApartamento())) {
			throw new IllegalArgumentException("No puedes modificar recibos de otro apartamento");
		}
		recibo.setIdEstadoRecibo(2);
		repository.save(recibo);
	}

	@Transactional
	public int enviarMasivo(List<Integer> ids) {
		return enviarMasivoInterno(ids);
	}

	@Transactional
	public int enviarMasivoPorTorre(Integer torre) {
		List<Integer> ids = query.recibos("", null, null, torre, null).stream()
				.map(row -> ((Number) row[0]).intValue())
				.toList();
		if (ids.isEmpty()) {
			throw new IllegalArgumentException("La torre no tiene recibos para enviar");
		}
		return enviarMasivoInterno(ids);
	}

	private int enviarMasivoInterno(List<Integer> ids) {
		int enviados = 0;
		for (Integer id : ids) {
			Recibo recibo = repository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException(RECIBO_NO_ENCONTRADO));
			TipoServicio servicio = servicioRepository.findById(recibo.getIdTipoServicio())
					.orElseThrow(() -> new IllegalArgumentException(SERVICIO_INVALIDO));
			for (var usuario : usuarioRepository.findActivosByApartamento(recibo.getIdApartamento())) {
				Notificacion notificacion = new Notificacion();
				notificacion.setTitulo("Nuevo recibo disponible");
				notificacion.setMensaje("Tienes un recibo de " + servicio.getNombre()
						+ " correspondiente al periodo " + recibo.getPeriodo()
						+ ". Consulta el detalle en la sección Recibos.");
				notificacion.setLeida(false);
				notificacion.setFecha(LocalDateTime.now(COLOMBIA_ZONE));
				notificacion.setIdUsuario(usuario.getIdUsuario());
				notificacionRepository.save(notificacion);
				enviados++;
			}
		}
		return enviados;
	}

	@Transactional
	public void eliminar(Integer id) {
		repository.deleteById(id);
	}
}
