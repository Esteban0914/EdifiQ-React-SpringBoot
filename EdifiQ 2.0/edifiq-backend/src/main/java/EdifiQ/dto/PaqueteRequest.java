package EdifiQ.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PaqueteRequest(Integer idPaquete, @NotBlank String descripcion,
                             @NotBlank String remitente, LocalDateTime fechaRecepcion,
                             LocalDateTime fechaEntrega, Integer idEstadoPaquete,
                             @NotNull Integer idApartamento, Integer idPersona) {}
