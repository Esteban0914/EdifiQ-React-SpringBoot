package EdifiQ.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import EdifiQ.dto.VisitaRequest;
import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.ApartmentScopeService;
import EdifiQ.service.VisitaService;
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
@RequestMapping("/api/visitas")
public class VisitaController {

	private final VisitaService service;
	private final ApartmentScopeService scope;

	public VisitaController(VisitaService service, ApartmentScopeService scope) {
		this.service = service;
		this.scope = scope;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE', 'RESIDENTE')")
	public List<Map<String, Object>> list(
			@RequestParam(defaultValue = "") String q,
			@RequestParam(required = false) Integer estado,
			@RequestParam(required = false) Integer tipo,
			@RequestParam(required = false) Integer torre,
			@RequestParam(required = false) LocalDate fechaDesde,
			@RequestParam(required = false) LocalDate fechaHasta,
			Authentication authentication
	) {
		Integer apartment = residentApartment(authentication);
		return service.buscar(q, estado, tipo, torre, fechaDesde, fechaHasta, apartment);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE', 'RESIDENTE')")
	public Map<String, Object> save(@Valid @RequestBody VisitaRequest request, Authentication authentication) {
		if (isResident(authentication)) {
			Integer apartment = residentApartment(authentication);
			if (apartment == null || !apartment.equals(request.idApartamento())) {
				throw new IllegalArgumentException("Solo puedes registrar visitas para tu apartamento");
			}
		}
		service.guardar(request);
		return Map.of("message", "Visita guardada correctamente");
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public Map<String, Object> update(@PathVariable Integer id, @Valid @RequestBody VisitaRequest request) {
		service.guardar(new VisitaRequest(
				id, request.idTipoVisita(), request.idTipoDocumento(), request.nombreVisitante(),
				request.documentoVisitante(), request.motivoVisita(), request.fechaIngreso(),
				request.fechaSalida(), request.idEstadoVisita(), request.autorizada(), request.idApartamento()
		));
		return Map.of("message", "Visita actualizada correctamente");
	}

	@PatchMapping("/{id}/estado/{estado}")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public void status(@PathVariable Integer id, @PathVariable Integer estado) {
		service.cambiarEstado(id, estado);
	}

	@PatchMapping("/{id}/autorizacion")
	@PreAuthorize("hasRole('RESIDENTE')")
	public void autorizacion(@PathVariable Integer id, @RequestParam boolean autorizada, Authentication authentication) {
		service.autorizar(id, autorizada, AppUserPrincipal.require(authentication).getIdPersona());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public void delete(@PathVariable Integer id) {
		service.eliminar(id);
	}

	private boolean isResident(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_RESIDENTE"));
	}

	private Integer residentApartment(Authentication authentication) {
		if (!isResident(authentication)) {
			return null;
		}
		return scope.activeApartmentForPerson(
				AppUserPrincipal.require(authentication).getIdPersona()
		);
	}
}
