package EdifiQ.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record VisitaRequest(Integer idVisita, @NotNull Integer idTipoVisita,
                            @NotNull Integer idTipoDocumento, @NotBlank String nombreVisitante,
                            @NotBlank String documentoVisitante, String motivoVisita,
                            LocalDateTime fechaIngreso, LocalDateTime fechaSalida,
                            Integer idEstadoVisita, Boolean autorizada, @NotNull Integer idApartamento) {}
