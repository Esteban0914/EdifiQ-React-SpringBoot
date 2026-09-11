package EdifiQ.controller;

import EdifiQ.dto.ReservaRequest;
import EdifiQ.security.AppUserPrincipal;
import EdifiQ.service.ApartmentScopeService;
import EdifiQ.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE','VIGILANTE')")
public class ReservaController {
    private static final String ROLE_RESIDENTE = "ROLE_RESIDENTE";
    private final ReservaService service;
    private final ApartmentScopeService scope;

    public ReservaController(ReservaService service, ApartmentScopeService scope) {
        this.service = service;
        this.scope = scope;
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestParam(defaultValue = "") String q,
                                          @RequestParam(required = false) Integer estado,
                                          @RequestParam(required = false) Integer zona,
                                          @RequestParam(required = false) Integer torre,
                                          @RequestParam(required = false) LocalDate fecha,
                                          Authentication authentication) {
        Integer apartment = null;
        if (hasRole(authentication, ROLE_RESIDENTE)) {
            apartment = scope.activeApartmentForPerson(AppUserPrincipal.require(authentication).getIdPersona());
        }
        return service.buscar(q, estado, zona, torre, fecha, apartment);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public Map<String, Object> save(@Valid @RequestBody ReservaRequest request, Authentication authentication) {
        Integer apartment = hasRole(authentication, ROLE_RESIDENTE) ? ownApartment(authentication) : null;
        service.guardar(scoped(request, authentication), apartment);
        return Map.of("message", "Reserva guardada correctamente");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public Map<String, Object> update(@PathVariable Integer id, @Valid @RequestBody ReservaRequest request, Authentication authentication) {
        Integer apartment = hasRole(authentication, ROLE_RESIDENTE) ? ownApartment(authentication) : null;
        service.guardar(scoped(request, authentication), apartment);
        return Map.of("message", "Reserva actualizada correctamente");
    }

    private ReservaRequest scoped(ReservaRequest request, Authentication authentication) {
        if (hasRole(authentication, ROLE_RESIDENTE)) {
            Integer apt = ownApartment(authentication);
            return new ReservaRequest(request.idReserva(), request.fechaReserva(), request.horaInicio(), request.horaFin(),
                    request.cantidadInvitados(), 1, request.idZona(), apt);
        }
        return request;
    }

    @PatchMapping("/{id}/estado/{estado}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void cambiarEstado(@PathVariable Integer id, @PathVariable Integer estado) {
        service.cambiarEstado(id, estado);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public void cancel(@PathVariable Integer id, Authentication authentication) {
        Integer apartment = hasRole(authentication, ROLE_RESIDENTE) ? ownApartment(authentication) : null;
        service.cancelar(id, apartment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void delete(@PathVariable Integer id) { service.eliminar(id); }

    private Integer ownApartment(Authentication authentication) {
        Integer apt = scope.activeApartmentForPerson(
                AppUserPrincipal.require(authentication).getIdPersona()
        );
        if (apt == null) throw new IllegalArgumentException("El usuario no tiene un apartamento activo asociado");
        return apt;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
