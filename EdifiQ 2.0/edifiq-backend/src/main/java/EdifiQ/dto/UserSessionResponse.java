package EdifiQ.dto;

public record UserSessionResponse(Integer idUsuario, Integer idPersona, String username,
                                  String nombreCompleto, Integer idRol, String rol) {}
