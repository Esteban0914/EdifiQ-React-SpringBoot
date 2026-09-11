package EdifiQ.dto;
import jakarta.validation.constraints.Email; import jakarta.validation.constraints.NotBlank;
public record VigilanteRequest(Integer idUsuario,Integer idPersona,@NotBlank String numeroDocumento,@NotBlank String nombres,@NotBlank String apellidos,String telefono,@Email String correo,@NotBlank String username,String password,Boolean activo) {}
