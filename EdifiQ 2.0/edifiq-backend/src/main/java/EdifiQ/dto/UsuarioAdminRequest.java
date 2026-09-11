package EdifiQ.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UsuarioAdminRequest(
        Integer idUsuario,
        Integer idPersona,
        Integer idTipoDocumento,
        @NotBlank String username,
        String password,
        Integer idRol,
        Integer idEstadoUsuario,
        @NotBlank String numeroDocumento,
        @NotBlank String nombres,
        @NotBlank String apellidos,
        String telefono,
        @Email String correo,
        Boolean activo,
        Integer idApartamento,
        Integer idTipoResidente,
        LocalDate fechaIngreso
) {}
