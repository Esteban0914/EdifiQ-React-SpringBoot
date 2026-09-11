package EdifiQ.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import EdifiQ.dto.PaqueteRequest;
import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.ApartmentScopeService;
import EdifiQ.service.PaqueteService;
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
@RequestMapping("/api/paquetes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE', 'RESIDENTE')")
public class PaqueteController {

	private final PaqueteService service;
	private final ApartmentScopeService scope;

	public PaqueteController(PaqueteService service, ApartmentScopeService scope) {
		this.service = service;
		this.scope = scope;
	}

	@GetMapping
	public List<Map<String, Object>> list(
			@RequestParam(defaultValue = "") String q,
			@RequestParam(required = false) Integer estado,
			@RequestParam(required = false) Integer torre,
			@RequestParam(required = false) LocalDate fecha,
			Authentication authentication
	) {
		Integer apartment = null;
		if (authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_RESIDENTE"))) {
			apartment = scope.activeApartmentForPerson(
					AppUserPrincipal.require(authentication).getIdPersona()
			);
		}
		return service.buscar(q, estado, torre, fecha, apartment);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public Map<String, Object> save(@Valid @RequestBody PaqueteRequest request) {
		service.guardar(request);
		return Map.of("message", "Paquete guardado correctamente");
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public Map<String, Object> update(@PathVariable Integer id, @Valid @RequestBody PaqueteRequest request) {
		service.guardar(new PaqueteRequest(
				id, request.descripcion(), request.remitente(), request.fechaRecepcion(),
				request.fechaEntrega(), request.idEstadoPaquete(), request.idApartamento(), request.idPersona()
		));
		return Map.of("message", "Paquete actualizado correctamente");
	}

	@PatchMapping("/{id}/entregar")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public void deliver(@PathVariable Integer id, @RequestParam(required = false) Integer idPersona) {
		service.entregar(id, idPersona);
	}

	@GetMapping("/{id}/personas")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public List<Map<String, Object>> personas(@PathVariable Integer id) {
		return service.personasDelPaquete(id);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public void delete(@PathVariable Integer id) {
		service.eliminar(id);
	}
}
