package EdifiQ.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReciboMasivoRequest(@NotNull Integer idTipoServicio,
                                  @NotBlank String periodo,
                                  @NotNull @DecimalMin("0.0") BigDecimal valor,
                                  @NotNull LocalDate fechaEmision,
                                  @NotNull LocalDate fechaVencimiento,
                                  Integer idEstadoRecibo,
                                  @NotNull Integer idTorre) {}