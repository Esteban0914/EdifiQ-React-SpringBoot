package EdifiQ.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ZonaComunRequest(
        Integer idZona,
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Size(max = 200) String descripcion
) {}
