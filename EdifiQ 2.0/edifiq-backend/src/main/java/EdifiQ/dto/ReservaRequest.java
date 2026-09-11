package EdifiQ.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest(Integer idReserva, @NotNull LocalDate fechaReserva,
                             @NotNull LocalTime horaInicio, @NotNull LocalTime horaFin,
                             Integer cantidadInvitados, Integer idEstadoReserva,
                             @NotNull Integer idZona, @NotNull Integer idApartamento) {}
