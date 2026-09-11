package EdifiQ.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class AppUserPrincipalTest {

    @Test
    void shouldExposeUserIdentityAndRoleData() {
        AppUserPrincipal principal = new AppUserPrincipal(
                10,
                20,
                "admin01",
                "secret",
                "Ana Gómez",
                "Administrador",
                1,
                true
        );

        assertEquals(10, principal.getIdUsuario());
        assertEquals(20, principal.getIdPersona());
        assertEquals("Ana Gómez", principal.getNombreCompleto());
        assertEquals("Administrador", principal.getRol());
        assertEquals(1, principal.getIdRol());
        assertEquals("admin01", principal.getUsername());
        assertEquals("secret", principal.getPassword());
    }

    @Test
    void shouldCreateAuthorityFromRoleName() {
        AppUserPrincipal principal = new AppUserPrincipal(
                1,
                2,
                "guard01",
                "pass",
                "Luis Ruiz",
                "Vigilante",
                2,
                true
        );

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_VIGILANTE", authorities.iterator().next().getAuthority());
    }

    @Test
    void shouldReportAccountStateAsEnabledAndNonExpired() {
        AppUserPrincipal principal = new AppUserPrincipal(
                1,
                2,
                "res01",
                "pass",
                "Marta Torres",
                "Residente",
                3,
                false
        );

        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());
        assertFalse(principal.isEnabled());
    }
}
