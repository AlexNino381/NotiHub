package notihub.canales;

import notihub.canales.externos.ServicioCorreo;
import notihub.canales.externos.ServicioExternoException;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * PATRÓN ADAPTER - rol "Adapter" (adapta ServicioCorreo a CanalNotificacion).
 */
public class AdaptadorCorreo implements CanalNotificacion {

    private final ServicioCorreo servicio; // rol "Adaptee"

    public AdaptadorCorreo(ServicioCorreo servicio) {
        this.servicio = servicio;
    }

    @Override
    public TipoCanal getTipo() {
        return TipoCanal.CORREO;
    }

    @Override
    public ResultadoEnvio enviar(Usuario destinatario, Mensaje mensaje) {
        if (FormatoTexto.estaVacio(destinatario.getCorreo())) {
            return ResultadoEnvio.fallo(getTipo(), "El usuario no tiene correo registrado");
        }
        try {
            servicio.enviarCorreo(destinatario.getCorreo(), mensaje.asunto(), mensaje.cuerpo());
            return ResultadoEnvio.exito(getTipo(), "Correo enviado a " + destinatario.getCorreo());
        } catch (ServicioExternoException e) {
            return ResultadoEnvio.fallo(getTipo(), e.getMessage());
        }
    }
}
