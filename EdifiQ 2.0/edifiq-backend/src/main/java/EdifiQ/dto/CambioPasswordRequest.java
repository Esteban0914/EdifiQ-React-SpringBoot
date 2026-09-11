package EdifiQ.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioPasswordRequest(
        @NotBlank String passwordActual,
        @NotBlank @Size(min = 6, max = 100) String nuevaPassword
) {}
