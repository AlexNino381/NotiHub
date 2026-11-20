package notihub.nucleo;

import notihub.canales.CanalNotificacion;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * Canal de prueba que falla un número exacto de veces y luego funciona.
 * Sirve para probar los reintentos sin depender del azar.
 */
class CanalFalso implements CanalNotificacion {

    private final TipoCanal tipo;
    private int fallosPendientes;
    private int llamadas;

    CanalFalso(TipoCanal tipo, int fallosPendientes) {
        this.tipo = tipo;
        this.fallosPendientes = fallosPendientes;
    }

    @Override
    public TipoCanal getTipo() {
        return tipo;
    }

    @Override
    public ResultadoEnvio enviar(Usuario destinatario, Mensaje mensaje) {
        llamadas++;
        if (fallosPendientes > 0) {
            fallosPendientes--;
            return ResultadoEnvio.fallo(tipo, "fallo simulado");
        }
        return ResultadoEnvio.exito(tipo, "enviado");
    }

    int getLlamadas() {
        return llamadas;
    }
}
