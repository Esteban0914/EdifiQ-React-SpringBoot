package EdifiQ.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ApartamentoRequest(
        Integer idApartamento,
        @NotBlank @Size(max = 10) String numeroApartamento,
        @NotNull @PositiveOrZero Integer piso,
        @NotNull Integer idTorre,
        Boolean activo
) {}
