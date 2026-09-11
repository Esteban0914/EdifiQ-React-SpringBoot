package EdifiQ.dto;

import java.time.LocalDate;

public record PersonaResponse(Integer id, Integer idTipoDocumento, String tipoDocumento,
                              String numeroDocumento, String nombres, String apellidos,
                              String telefono, String correo, Boolean activo,
                              Integer idApartamento, String apartamento,
                              Integer idTorre, String torre, Integer idTipoResidente,
                              String tipoResidente, LocalDate fechaIngreso, LocalDate fechaSalida) {
    public String nombreCompleto() { return nombres + " " + apellidos; }
}
