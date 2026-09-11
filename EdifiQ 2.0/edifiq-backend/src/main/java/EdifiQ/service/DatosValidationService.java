package EdifiQ.service;

public final class DatosValidationService {
    private DatosValidationService() {}

    public static void validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) return;
        String value = telefono.trim();
        if (!value.matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono debe contener exactamente 10 dígitos");
        }
    }

    public static void validarDocumento(Integer tipoDocumento, String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }

        String value = documento.trim();
        switch (tipoDocumento == null ? -1 : tipoDocumento) {
            case 1 -> { // CC
                if (!value.matches("\\d{6,10}"))
                    throw new IllegalArgumentException("La cédula de ciudadanía debe tener entre 6 y 10 dígitos");
            }
            case 2 -> { // TI
                if (!value.matches("\\d{10,11}"))
                    throw new IllegalArgumentException("La tarjeta de identidad debe tener entre 10 y 11 dígitos");
            }
            case 3 -> { // CE
                if (!value.matches("\\d{6,10}"))
                    throw new IllegalArgumentException("La cédula de extranjería debe tener entre 6 y 10 dígitos");
            }
            case 4 -> { // Pasaporte
                if (!value.matches("[A-Za-z0-9]{6,16}"))
                    throw new IllegalArgumentException("El pasaporte debe tener entre 6 y 16 caracteres alfanuméricos");
            }
            case 5 -> { // NIT
                if (!value.matches("\\d{10}"))
                    throw new IllegalArgumentException("El NIT debe tener 10 dígitos, incluido el dígito de verificación");
            }
            default -> {
                if (!value.matches("[A-Za-z0-9]{6,16}"))
                    throw new IllegalArgumentException("El documento debe tener entre 6 y 16 caracteres alfanuméricos");
            }
        }
    }
}
