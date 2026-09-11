package EdifiQ.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReciboRequest(Integer idRecibo, @NotNull Integer idTipoServicio,
                            @NotBlank String periodo, @NotNull @DecimalMin("0.0") BigDecimal valor,
                            @NotNull LocalDate fechaEmision, @NotNull LocalDate fechaVencimiento,
                            Integer idEstadoRecibo, @NotNull Integer idApartamento) {}
