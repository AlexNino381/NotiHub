package notihub.registro;

import java.time.LocalDateTime;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * Constancia de un envío (exitoso o fallido). El canal es nulo cuando el mensaje
 * no pudo asignarse a ningún canal.
 */
public record RegistroEnvio(LocalDateTime fecha, String usuario, TipoCanal canal,
                            boolean exitoso, int intentos, String detalle) {

    public static RegistroEnvio desde(Usuario destinatario, ResultadoEnvio resultado) {
        return new RegistroEnvio(LocalDateTime.now(), destinatario.getNombre(), resultado.canal(),
                resultado.exitoso(), resultado.intentos(), resultado.detalle());
    }

    public String estado() {
        return exitoso ? "ENVIADO" : "FALLIDO";
    }
}
