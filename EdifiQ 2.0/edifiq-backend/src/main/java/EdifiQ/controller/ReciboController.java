package EdifiQ.controller;

import java.util.List;
import java.util.Map;

import EdifiQ.dto.ReciboMasivoRequest;
import EdifiQ.dto.ReciboRequest;
import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.ApartmentScopeService;
import EdifiQ.service.ReciboService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recibos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESIDENTE')")
public class ReciboController {

	private final ReciboService service;
	private final ApartmentScopeService scope;

	public ReciboController(ReciboService service, ApartmentScopeService scope) {
		this.service = service;
		this.scope = scope;
	}

	@GetMapping
	public List<Map<String, Object>> list(
			@RequestParam(defaultValue = "") String q,
			@RequestParam(required = false) Integer estado,
			@RequestParam(required = false) Integer servicio,
			@RequestParam(required = false) Integer torre,
			Authentication authentication
	) {
		Integer apartment = residentApartment(authentication);
		return service.buscar(q, estado, servicio, torre, apartment);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public Map<String, Object> save(@Valid @RequestBody ReciboRequest request) {
		service.guardar(request);
		return Map.of("message", "Recibo guardado correctamente");
	}

	@PostMapping("/torre")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public Map<String, Object> saveForTower(@Valid @RequestBody ReciboMasivoRequest request) {
		return Map.of("creados", service.guardarPorTorre(request));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public Map<String, Object> update(
			@PathVariable Integer id,
			@Valid @RequestBody ReciboRequest request
	) {
		service.guardar(new ReciboRequest(
				id,
				request.idTipoServicio(),
				request.periodo(),
				request.valor(),
				request.fechaEmision(),
				request.fechaVencimiento(),
				request.idEstadoRecibo(),
				request.idApartamento()
		));
		return Map.of("message", "Recibo actualizado correctamente");
	}

	@PostMapping("/enviar-masivo")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public Map<String, Object> sendBulk(@RequestBody List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			throw new IllegalArgumentException("Selecciona al menos un recibo");
		}
		return Map.of("enviados", service.enviarMasivo(ids));
	}

	@PatchMapping("/{id}/pagar")
	public void pay(@PathVariable Integer id, Authentication authentication) {
		service.pagar(id, residentApartment(authentication));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public void delete(@PathVariable Integer id) {
		service.eliminar(id);
	}

	private Integer residentApartment(Authentication authentication) {
		boolean resident = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_RESIDENTE"));
		if (!resident) {
			return null;
		}
		if (!(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
			return null;
		}
		return scope.activeApartmentForPerson(principal.getIdPersona());
	}
}
