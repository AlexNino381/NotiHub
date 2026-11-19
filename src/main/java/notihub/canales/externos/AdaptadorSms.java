package notihub.canales;

import notihub.canales.externos.ServicioExternoException;
import notihub.canales.externos.ServicioSms;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * PATRÓN ADAPTER - rol "Adapter" (adapta ServicioSms a CanalNotificacion).
 * Une asunto y cuerpo en un solo texto y lo ajusta al límite de 160 caracteres.
 */
public class AdaptadorSms implements CanalNotificacion {

    private final ServicioSms servicio; // rol "Adaptee"

    public AdaptadorSms(ServicioSms servicio) {
        this.servicio = servicio;
    }

    @Override
    public TipoCanal getTipo() {
        return TipoCanal.SMS;
    }

    @Override
    public ResultadoEnvio enviar(Usuario destinatario, Mensaje mensaje) {
        if (FormatoTexto.estaVacio(destinatario.getTelefono())) {
            return ResultadoEnvio.fallo(getTipo(), "El usuario no tiene teléfono registrado");
        }
        String texto = mensaje.asunto().isBlank()
                ? mensaje.cuerpo()
                : mensaje.asunto() + ": " + mensaje.cuerpo();
        texto = FormatoTexto.recortar(texto, ServicioSms.LIMITE_CARACTERES);
        try {
            servicio.enviarSms(destinatario.getTelefono(), texto);
            return ResultadoEnvio.exito(getTipo(), "SMS enviado a " + destinatario.getTelefono());
        } catch (ServicioExternoException e) {
            return ResultadoEnvio.fallo(getTipo(), e.getMessage());
        }
    }
}
