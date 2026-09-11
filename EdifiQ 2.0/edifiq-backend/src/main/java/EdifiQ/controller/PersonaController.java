package EdifiQ.controller;

import EdifiQ.dto.PersonaRequest;
import EdifiQ.dto.PersonaResponse;
import EdifiQ.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','VIGILANTE')")
public class PersonaController {
    private final PersonaService service;

    public PersonaController(PersonaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PersonaResponse> list(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) Integer tipo,
            @RequestParam(required = false) Integer estado,
            @RequestParam(required = false) Integer torre) {
        return service.buscar(q, tipo, estado, torre);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object stats() {
        return service.stats();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Map<String, Object> save(@Valid @RequestBody PersonaRequest request) {
        PersonaResponse saved = service.guardar(request);
        return Map.of("data", saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PersonaResponse update(@PathVariable Integer id, @Valid @RequestBody PersonaRequest req) {
        return service.guardar(
                new PersonaRequest(
                        id,
                        req.idTipoDocumento(),
                        req.numeroDocumento(),
                        req.nombres(),
                        req.apellidos(),
                        req.telefono(),
                        req.correo(),
                        req.activo(),
                        req.idApartamento(),
                        req.idTipoResidente(),
                        req.fechaIngreso()));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void toggle(@PathVariable Integer id) {
        service.toggle(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void delete(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
