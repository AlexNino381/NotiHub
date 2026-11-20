package notihub.reglas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import notihub.canales.AdaptadorCorreo;
import notihub.canales.AdaptadorSms;
import notihub.canales.ResultadoEnvio;
import notihub.canales.externos.ServicioCorreo;
import notihub.canales.externos.ServicioSms;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import notihub.nucleo.CentralNotificaciones;
import org.junit.jupiter.api.Test;

class EstrategiasEnvioTest {

    private final List<TipoCanal> aceptados = List.of(TipoCanal.SMS, TipoCanal.CORREO, TipoCanal.MENSAJERIA);

    @Test
    void laReglaUrgenteUsaTodosLosCanalesEnOrden() {
        assertEquals(aceptados, new EnvioUrgente().elegirCanales(aceptados));
    }

    @Test
    void laReglaInformativaUsaSoloElCanalPreferido() {
        assertEquals(List.of(TipoCanal.SMS), new EnvioInformativo().elegirCanales(aceptados));
    }

    @Test
    void laReglaInformativaSinCanalesNoElige() {
        assertTrue(new EnvioInformativo().elegirCanales(List.of()).isEmpty());
    }

    @Test
    void laReglaUrgenteSinCanalesNoElige() {
        assertTrue(new EnvioUrgente().elegirCanales(List.of()).isEmpty());
    }

    @Test
    void sePuedeAgregarUnaReglaNuevaSinModificarLaFachada() {
        ServicioCorreo correo = new ServicioCorreo();
        ServicioSms sms = new ServicioSms();
        CentralNotificaciones central = new CentralNotificaciones(
                List.of(new AdaptadorCorreo(correo), new AdaptadorSms(sms)));
        Usuario ana = new Usuario("Ana", "ana@mail.com", "3001112233", null,
                List.of(TipoCanal.SMS, TipoCanal.CORREO));

        // Regla nueva: los informativos salen por el ÚLTIMO canal de la lista
        central.registrarEstrategia(TipoMensaje.INFORMATIVO, canales -> List.of(canales.get(canales.size() - 1)));

        List<ResultadoEnvio> resultados = central.enviar(ana,
                new Mensaje(TipoMensaje.INFORMATIVO, "Aviso", "Hay novedades"));

        assertEquals(1, resultados.size());
        assertEquals(TipoCanal.CORREO, resultados.get(0).canal());
        assertTrue(sms.getEnviados().isEmpty());
    }
}
