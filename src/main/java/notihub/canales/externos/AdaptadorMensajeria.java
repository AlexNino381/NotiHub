package notihub.canales;

import notihub.canales.externos.ServicioExternoException;
import notihub.canales.externos.ServicioMensajeria;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * PATRÓN ADAPTER - rol "Adapter" (adapta ServicioMensajeria a CanalNotificacion).
 * Presenta el asunto en negrita (*asunto*) y ajusta el texto a 280 caracteres.
 */
public class AdaptadorMensajeria implements CanalNotificacion {

    private final ServicioMensajeria servicio; // rol "Adaptee"

    public AdaptadorMensajeria(ServicioMensajeria servicio) {
        this.servicio = servicio;
    }

    @Override
    public TipoCanal getTipo() {
        return TipoCanal.MENSAJERIA;
    }

    @Override
    public ResultadoEnvio enviar(Usuario destinatario, Mensaje mensaje) {
        if (FormatoTexto.estaVacio(destinatario.getIdMensajeria())) {
            return ResultadoEnvio.fallo(getTipo(), "El usuario no tiene identificador de mensajería");
        }
        String texto = mensaje.asunto().isBlank()
                ? mensaje.cuerpo()
                : "*" + mensaje.asunto() + "*\n" + mensaje.cuerpo();
        texto = FormatoTexto.recortar(texto, ServicioMensajeria.LIMITE_CARACTERES);
        try {
            servicio.publicarMensaje(destinatario.getIdMensajeria(), texto);
            return ResultadoEnvio.exito(getTipo(), "Mensaje enviado al chat " + destinatario.getIdMensajeria());
        } catch (ServicioExternoException e) {
            return ResultadoEnvio.fallo(getTipo(), e.getMessage());
        }
    }
}
