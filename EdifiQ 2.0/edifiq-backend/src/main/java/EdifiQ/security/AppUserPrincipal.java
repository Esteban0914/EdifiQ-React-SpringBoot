package EdifiQ.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserPrincipal implements UserDetails {
    private final Integer idUsuario;
    private final Integer idPersona;
    private final String username;
    private final String password;
    private final String nombreCompleto;
    private final String rol;
    private final Integer idRol;
    private final boolean activo;

    @SuppressWarnings("java:S107")
    public AppUserPrincipal(Integer idUsuario, Integer idPersona, String username, String password,
                            String nombreCompleto, String rol, Integer idRol, boolean activo) {
        this.idUsuario = idUsuario;
        this.idPersona = idPersona;
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.idRol = idRol;
        this.activo = activo;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public Integer getIdPersona() { return idPersona; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRol() { return rol; }
    public Integer getIdRol() { return idRol; }

    public static AppUserPrincipal require(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new IllegalStateException("La autenticación del usuario no es válida");
        }
        return principal;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + rol.toUpperCase().replace(' ', '_'));
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return activo; }
}
