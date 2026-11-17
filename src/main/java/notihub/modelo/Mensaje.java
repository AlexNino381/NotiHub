package notihub.modelo;

/**
 * Mensaje ya listo para enviarse. Es inmutable.
 */
public record Mensaje(TipoMensaje tipo, String asunto, String cuerpo) {

    public Mensaje {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de mensaje es obligatorio");
        }
        if (cuerpo == null || cuerpo.isBlank()) {
            throw new IllegalArgumentException("El cuerpo del mensaje es obligatorio");
        }
        if (asunto == null) {
            asunto = "";
        }
    }
}
