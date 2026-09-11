package EdifiQ.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TorreRequest(
        Integer idTorre,
        @NotBlank @Size(max = 20) String nombreTorre,
        @Min(1) @Max(50) Integer cantidadTorres,
        @Min(1) @Max(26) Integer pisos,
        @Min(1) @Max(20) Integer apartamentosPorPiso
) {}
