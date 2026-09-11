package EdifiQ.controller;

import EdifiQ.dto.UsuarioAdminRequest;
import EdifiQ.service.UsuarioAdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioAdminController {
    private final UsuarioAdminService service;

    public UsuarioAdminController(UsuarioAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Integer rol,
            @RequestParam(required = false) Integer estado) {
        return service.buscar(q, rol, estado);
    }

    @PostMapping
    public Map<String, String> create(@Valid @RequestBody UsuarioAdminRequest request) {
        service.guardar(request);
        return Map.of("message", "Usuario creado correctamente");
    }

    @PutMapping("/{id}")
    public Map<String, String> update(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioAdminRequest request
    ) {
        UsuarioAdminRequest updatedRequest = new UsuarioAdminRequest(
                id,
                request.idPersona(),
                request.idTipoDocumento(),
                request.username(),
                request.password(),
                request.idRol(),
                request.idEstadoUsuario(),
                request.numeroDocumento(),
                request.nombres(),
                request.apellidos(),
                request.telefono(),
                request.correo(),
                request.activo(),
                request.idApartamento(),
                request.idTipoResidente(),
                request.fechaIngreso()
        );
        service.guardar(updatedRequest);
        return Map.of("message", "Usuario actualizado correctamente");
    }

    @PatchMapping("/{id}/estado")
    public void toggle(@PathVariable Integer id) {
        service.toggle(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
