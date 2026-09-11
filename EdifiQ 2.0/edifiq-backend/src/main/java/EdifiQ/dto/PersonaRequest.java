package EdifiQ.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonaRequest(Integer idPersona, @NotNull Integer idTipoDocumento,
                             @NotBlank String numeroDocumento, @NotBlank String nombres,
                             @NotBlank String apellidos, String telefono, @Email String correo,
                             Boolean activo, Integer idApartamento, Integer idTipoResidente,
                             LocalDate fechaIngreso) {}
