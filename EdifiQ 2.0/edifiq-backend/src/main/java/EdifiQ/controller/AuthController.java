package EdifiQ.controller;

import EdifiQ.dto.AuthRequest;
import EdifiQ.dto.UserSessionResponse;
import EdifiQ.security.AppUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
        public ResponseEntity<Map<String, UserSessionResponse>> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username().trim(),
                        request.password()
                )
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        AppUserPrincipal principal = AppUserPrincipal.require(authentication);

        return ResponseEntity.ok(
                Map.of(
                        "user",
                        toResponse(principal)
                )
        );
    }

    @GetMapping("/me")
        public ResponseEntity<UserSessionResponse> me(Authentication authentication) {

        /*
         * Cuando el usuario todavía no ha iniciado sesión,
         * Spring Security puede enviar un Authentication anónimo.
         *
         * Antes se intentaba hacer directamente:
         *
         * (AppUserPrincipal) authentication.getPrincipal()
         *
         * y eso provocaba un error 500.
         *
         * Ahora devolvemos 401 correctamente.
         */

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                toResponse(principal)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        SecurityContextHolder.clearContext();

        var session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }

    private UserSessionResponse toResponse(AppUserPrincipal principal) {

        return new UserSessionResponse(
                principal.getIdUsuario(),
                principal.getIdPersona(),
                principal.getUsername(),
                principal.getNombreCompleto(),
                principal.getIdRol(),
                principal.getRol()
        );
    }
}