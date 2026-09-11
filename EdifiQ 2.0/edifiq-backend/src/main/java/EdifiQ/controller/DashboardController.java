package EdifiQ.controller;

import java.util.Map;

import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final DashboardService service;

	public DashboardController(DashboardService service) {
		this.service = service;
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public Map<String, Object> admin() {
		return service.admin();
	}

	@GetMapping("/guard")
	@PreAuthorize("hasRole('VIGILANTE')")
	public Map<String, Object> guard() {
		return service.guard();
	}

	@GetMapping("/resident")
	@PreAuthorize("hasRole('RESIDENTE')")
	public Map<String, Object> resident(Authentication authentication) {
		return service.resident(AppUserPrincipal.require(authentication).getIdPersona());
	}
}
