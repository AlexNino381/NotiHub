package notihub.canales;

import notihub.modelo.TipoCanal;

/**
 * Resultado de un intento de envío por un canal. El canal puede ser nulo cuando
 * el envío ni siquiera pudo asignarse a un canal (por ejemplo, el usuario no
 * acepta ninguno).
 */
public record ResultadoEnvio(TipoCanal canal, boolean exitoso, String detalle) {

    public static ResultadoEnvio exito(TipoCanal canal, String detalle) {
        return new ResultadoEnvio(canal, true, detalle);
    }

    public static ResultadoEnvio fallo(TipoCanal canal, String detalle) {
        return new ResultadoEnvio(canal, false, detalle);
    }
}
