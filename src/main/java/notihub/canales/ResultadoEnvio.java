package notihub.canales;

import notihub.modelo.TipoCanal;

/**
 * Resultado de un envío por un canal, con el número de intentos que hicieron falta.
 * El canal puede ser nulo cuando el envío ni siquiera pudo asignarse a un canal
 * (por ejemplo, el usuario no acepta ninguno).
 */
public record ResultadoEnvio(TipoCanal canal, boolean exitoso, String detalle, int intentos) {

    public static ResultadoEnvio exito(TipoCanal canal, String detalle) {
        return new ResultadoEnvio(canal, true, detalle, 1);
    }

    public static ResultadoEnvio fallo(TipoCanal canal, String detalle) {
        return new ResultadoEnvio(canal, false, detalle, 1);
    }

    /** Copia del resultado indicando cuántos intentos se hicieron en total. */
    public ResultadoEnvio conIntentos(int intentos) {
        return new ResultadoEnvio(canal, exitoso, detalle, intentos);
    }
}
