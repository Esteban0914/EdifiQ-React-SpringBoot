package EdifiQ.controller;

import java.util.List;
import java.util.Map;

import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.AsociadosService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asociados")
public class AsociadosController {

	private final AsociadosService service;

	public AsociadosController(AsociadosService service) {
		this.service = service;
	}

	@GetMapping("/mios")
	@PreAuthorize("hasRole('RESIDENTE')")
	public List<Map<String, Object>> mios(Authentication authentication) {
		AppUserPrincipal principal = requirePrincipal(authentication);
		return service.listarParaPersona(principal.getIdPersona());
	}

	@PostMapping("/mios")
	@PreAuthorize("hasRole('RESIDENTE')")
	public Map<String, Object> agregar(
			@RequestBody Map<String, Object> body,
			Authentication authentication
	) {
		AppUserPrincipal principal = requirePrincipal(authentication);
		return service.agregar(
				principal.getIdPersona(),
			integerValue(body.get("idTipoDocumento")),
				String.valueOf(body.get("numeroDocumento")),
				String.valueOf(body.get("nombres")),
				String.valueOf(body.get("apellidos")),
				body.get("telefono") == null ? null : String.valueOf(body.get("telefono")),
				body.get("correo") == null ? null : String.valueOf(body.get("correo"))
		);
	}

	@DeleteMapping("/mios/{id}")
	@PreAuthorize("hasRole('RESIDENTE')")
	public void eliminar(@PathVariable Integer id, Authentication authentication) {
		AppUserPrincipal principal = requirePrincipal(authentication);
		service.eliminar(principal.getIdPersona(), id);
	}

	@GetMapping("/apartamento/{id}")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VIGILANTE')")
	public List<Map<String, Object>> apartamento(@PathVariable Integer id) {
		return service.listar(id);
	}

	@GetMapping("/persona/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public List<Map<String, Object>> persona(@PathVariable Integer id) {
		return service.listarParaPersona(id);
	}

	private AppUserPrincipal requirePrincipal(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
			throw new IllegalStateException("La autenticación del usuario no es válida");
		}
		return principal;
	}

	private Integer integerValue(Object value) {
		if (value instanceof Number number) {
			return number.intValue();
		}
		return Integer.valueOf(String.valueOf(value));
	}
}
