package EdifiQ.controller;

import EdifiQ.dto.CambioPasswordRequest;
import EdifiQ.dto.PerfilRequest;
import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {
    private final PerfilService service;

    public PerfilController(PerfilService service) { this.service = service; }

    @GetMapping
    public Map<String, Object> get(Authentication authentication) {
        return service.obtener(AppUserPrincipal.require(authentication).getIdUsuario());
    }

    @PutMapping
    public Map<String, String> update(@Valid @RequestBody PerfilRequest request, Authentication authentication) {
        service.actualizar(AppUserPrincipal.require(authentication).getIdUsuario(), request);
        return Map.of("message", "Perfil actualizado correctamente");
    }

    @PatchMapping("/password")
    public Map<String, String> password(@Valid @RequestBody CambioPasswordRequest request, Authentication authentication) {
        service.cambiarPassword(AppUserPrincipal.require(authentication).getIdUsuario(), request);
        return Map.of("message", "Contraseña actualizada correctamente");
    }
}
