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
import notihub.registro.ObservadorEnvio;
import notihub.registro.RegistroEnvio;
import notihub.reglas.EnvioInformativo;
import notihub.reglas.EnvioUrgente;
import notihub.reglas.EstrategiaEnvio;

/**
 * PATRÓN FACADE - rol "Facade": único punto de entrada del núcleo. La consola y la
 * interfaz gráfica solo llaman a {@link #enviar(Usuario, Mensaje)}.
 *
 * PATRÓN STRATEGY - rol "Context": la regla que decide por cuáles canales sale un
 * mensaje se busca según el tipo de mensaje; no está escrita aquí con un if.
 *
 * PATRÓN OBSERVER - rol "Subject": cada resultado de envío se avisa a los
 * observadores registrados (historial, archivo, consola...), que esta clase no
 * conoce en concreto.
 *
 * REINTENTOS: si un canal falla, se vuelve a intentar hasta {@code maxIntentos}
 * veces. Limitación conocida: no distingue fallos pasajeros de permanentes.
 */
public class CentralNotificaciones {

    public static final int INTENTOS_POR_DEFECTO = 3;

    private final Map<TipoCanal, CanalNotificacion> canales = new EnumMap<>(TipoCanal.class);
    private final Map<TipoMensaje, EstrategiaEnvio> estrategias = new EnumMap<>(TipoMensaje.class);
    private final List<ObservadorEnvio> observadores = new ArrayList<>();
    private final int maxIntentos;

    public CentralNotificaciones(List<CanalNotificacion> canalesDisponibles) {
        this(canalesDisponibles, INTENTOS_POR_DEFECTO);
    }

    public CentralNotificaciones(List<CanalNotificacion> canalesDisponibles, int maxIntentos) {
        if (canalesDisponibles == null || canalesDisponibles.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos un canal disponible");
        }
        if (maxIntentos < 1) {
            throw new IllegalArgumentException("Se necesita al menos un intento de envío");
        }
        this.maxIntentos = maxIntentos;
        for (CanalNotificacion canal : canalesDisponibles) {
            canales.put(canal.getTipo(), canal);
        }
        estrategias.put(TipoMensaje.URGENTE, new EnvioUrgente());
        estrategias.put(TipoMensaje.INFORMATIVO, new EnvioInformativo());
    }

    /** Asocia (o reemplaza) la regla de envío de un tipo de mensaje. */
    public void registrarEstrategia(TipoMensaje tipo, EstrategiaEnvio estrategia) {
        if (tipo == null || estrategia == null) {
            throw new IllegalArgumentException("El tipo de mensaje y la estrategia son obligatorios");
        }
        estrategias.put(tipo, estrategia);
    }

    /** Suscribe un observador que recibirá el registro de cada envío. */
    public void agregarObservador(ObservadorEnvio observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador es obligatorio");
        }
        observadores.add(observador);
    }

    /**
     * Envía el mensaje al usuario respetando los canales que acepta. Devuelve un
     * resultado por cada canal usado; un fallo en uno no impide usar los demás.
     */
    public List<ResultadoEnvio> enviar(Usuario destinatario, Mensaje mensaje) {
        EstrategiaEnvio estrategia = estrategias.get(mensaje.tipo());
        if (estrategia == null) {
            return List.of(registrar(destinatario, ResultadoEnvio.fallo(null,
                    "No hay una regla de envío para mensajes " + mensaje.tipo()).conIntentos(0)));
        }

        List<TipoCanal> aUsar = estrategia.elegirCanales(canalesAceptados(destinatario));
        if (aUsar.isEmpty()) {
            return List.of(registrar(destinatario, ResultadoEnvio.fallo(null,
                    "El usuario " + destinatario.getNombre()
                            + " no acepta ninguno de los canales disponibles").conIntentos(0)));
        }

        List<ResultadoEnvio> resultados = new ArrayList<>();
        for (TipoCanal tipo : aUsar) {
            ResultadoEnvio resultado = enviarConReintentos(canales.get(tipo), destinatario, mensaje);
            resultados.add(registrar(destinatario, resultado));
        }
        return resultados;
    }

    private ResultadoEnvio enviarConReintentos(CanalNotificacion canal, Usuario destinatario, Mensaje mensaje) {
        ResultadoEnvio resultado = null;
        for (int intento = 1; intento <= maxIntentos; intento++) {
            resultado = canal.enviar(destinatario, mensaje);
            if (resultado.exitoso()) {
                return resultado.conIntentos(intento);
            }
        }
        return resultado.conIntentos(maxIntentos);
    }

    /** Avisa a los observadores y devuelve el mismo resultado. */
    private ResultadoEnvio registrar(Usuario destinatario, ResultadoEnvio resultado) {
        RegistroEnvio registro = RegistroEnvio.desde(destinatario, resultado);
        for (ObservadorEnvio observador : observadores) {
            try {
                observador.alRegistrarEnvio(registro);
            } catch (RuntimeException e) {
                // Un observador defectuoso no debe impedir los envíos ni a los demás observadores
                System.err.println("Un observador de envíos falló: " + e.getMessage());
            }
        }
        return resultado;
    }

    /** Canales del usuario que además están disponibles, en su orden de preferencia. */
    private List<TipoCanal> canalesAceptados(Usuario usuario) {
        List<TipoCanal> aceptados = new ArrayList<>();
        for (TipoCanal canal : usuario.getCanalesPreferidos()) {
            if (canales.containsKey(canal)) {
                aceptados.add(canal);
            }
        }
        return aceptados;
    }
}
