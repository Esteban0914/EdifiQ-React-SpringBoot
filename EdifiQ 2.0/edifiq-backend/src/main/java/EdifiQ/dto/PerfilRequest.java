package EdifiQ.dto;

import jakarta.validation.constraints.Email;

public record PerfilRequest(
        String telefono,
        @Email
        String correo
        ) {

}
