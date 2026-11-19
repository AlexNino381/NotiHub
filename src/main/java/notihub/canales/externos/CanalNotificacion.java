package notihub.canales;

import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * PATRÓN ADAPTER - rol "Target".
 *
 * Interfaz común que NotiHub espera de cualquier canal. El resto del sistema solo
 * conoce esta interfaz, nunca los servicios externos concretos.
 */
public interface CanalNotificacion {

    TipoCanal getTipo();

    /**
     * Envía el mensaje al usuario por este canal. No lanza excepciones por fallos
     * del servicio externo: los reporta dentro del {@link ResultadoEnvio}.
     */
    ResultadoEnvio enviar(Usuario destinatario, Mensaje mensaje);
}
