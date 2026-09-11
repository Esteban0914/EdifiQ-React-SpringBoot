package EdifiQ.controller;

import EdifiQ.dto.ApartamentoRequest;
import EdifiQ.dto.TorreRequest;
import EdifiQ.dto.ZonaComunRequest;
import EdifiQ.service.EstructuraService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EstructuraController {
    private final EstructuraService service;


    public EstructuraController(EstructuraService service) {
        this.service = service;
    }

    @GetMapping("/torres")
    @PreAuthorize("isAuthenticated()")
    public Object torres() {
        return service.torres();
    }

    @PostMapping("/torres")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object guardarTorre(@Valid @RequestBody TorreRequest req) {
        return service.guardarTorre(req);
    }

    @PutMapping("/torres/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object actualizarTorre(@PathVariable Integer id, @Valid @RequestBody TorreRequest req) {
        return service.guardarTorre(new TorreRequest(id, req.nombreTorre(), 1, null, null));
    }

    @DeleteMapping("/torres/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminarTorre(@PathVariable Integer id) {
        service.eliminarTorre(id);
    }

    @GetMapping("/apartamentos")
    @PreAuthorize("isAuthenticated()")
    public Object apartamentos() {
        return service.apartamentos();
    }

    @PostMapping("/apartamentos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object guardarApartamento(@Valid @RequestBody ApartamentoRequest req) {
        return service.guardarApartamento(req);
    }

    @PutMapping("/apartamentos/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object actualizarApartamento(@PathVariable Integer id, @Valid @RequestBody ApartamentoRequest req) {
        return service.guardarApartamento(new ApartamentoRequest(id, req.numeroApartamento(), req.piso(), req.idTorre(), req.activo()));
    }

    @PatchMapping("/apartamentos/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void toggleApartamento(@PathVariable Integer id) {
        service.toggleApartamento(id);
    }

    @DeleteMapping("/apartamentos/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminarApartamento(@PathVariable Integer id) {
        service.eliminarApartamento(id);
    }

    @GetMapping("/zonas")
    @PreAuthorize("isAuthenticated()")
    public Object zonas() {
        return service.zonas();
    }

    @PostMapping("/zonas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object guardarZona(@Valid @RequestBody ZonaComunRequest req) {
        return service.guardarZona(req);
    }

    @PutMapping("/zonas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Object actualizarZona(@PathVariable Integer id, @Valid @RequestBody ZonaComunRequest req) {
        return service.guardarZona(new ZonaComunRequest(id, req.nombre(), req.descripcion()));
    }

    @DeleteMapping("/zonas/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminarZona(@PathVariable Integer id) {
        service.eliminarZona(id);
    }
}
