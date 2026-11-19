package notihub.nucleo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import notihub.canales.CanalNotificacion;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;

/**
 * PATRÓN FACADE - rol "Facade".
 *
 * Único punto de entrada del núcleo: la consola y la interfaz gráfica solo llaman
 * a {@link #enviar(Usuario, Mensaje)} y no conocen los canales, los adaptadores
 * ni las reglas de selección.
 *
 * VERSIÓN 1: la regla de selección de canales está escrita con un if dentro de
 * {@link #seleccionarCanales}. Es el "diseño ingenuo" que el día 3 se reemplaza
 * por el patrón Strategy.
 */
public class CentralNotificaciones {

    private final Map<TipoCanal, CanalNotificacion> canales = new EnumMap<>(TipoCanal.class);

    public CentralNotificaciones(List<CanalNotificacion> canalesDisponibles) {
        if (canalesDisponibles == null || canalesDisponibles.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos un canal disponible");
        }
        for (CanalNotificacion canal : canalesDisponibles) {
            canales.put(canal.getTipo(), canal);
        }
    }

    /**
     * Envía el mensaje al usuario respetando los canales que acepta. Devuelve un
     * resultado por cada canal usado; un fallo en uno no impide usar los demás.
     */
    public List<ResultadoEnvio> enviar(Usuario destinatario, Mensaje mensaje) {
        List<TipoCanal> aUsar = seleccionarCanales(destinatario, mensaje.tipo());
        if (aUsar.isEmpty()) {
            return List.of(ResultadoEnvio.fallo(null,
                    "El usuario " + destinatario.getNombre() + " no acepta ninguno de los canales disponibles"));
        }
        List<ResultadoEnvio> resultados = new ArrayList<>();
        for (TipoCanal tipo : aUsar) {
            resultados.add(canales.get(tipo).enviar(destinatario, mensaje));
        }
        return resultados;
    }

    private List<TipoCanal> seleccionarCanales(Usuario usuario, TipoMensaje tipo) {
        List<TipoCanal> aceptados = new ArrayList<>();
        for (TipoCanal canal : usuario.getCanalesPreferidos()) {
            if (canales.containsKey(canal)) {
                aceptados.add(canal);
            }
        }
        if (tipo == TipoMensaje.URGENTE || aceptados.isEmpty()) {
            return aceptados;
        }
        return List.of(aceptados.get(0));
    }
}
