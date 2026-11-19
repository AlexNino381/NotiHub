package notihub.canales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import notihub.canales.externos.ServicioCorreo;
import notihub.canales.externos.ServicioMensajeria;
import notihub.canales.externos.ServicioSms;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import org.junit.jupiter.api.Test;

class AdaptadoresTest {

    private final Usuario ana = new Usuario("Ana", "ana@mail.com", "3001112233", "ana_chat",
            List.of(TipoCanal.CORREO, TipoCanal.SMS, TipoCanal.MENSAJERIA));

    private final Mensaje corto = new Mensaje(TipoMensaje.INFORMATIVO, "Hola", "Bienvenida");

    @Test
    void todosLosAdaptadoresSeUsanComoCanalNotificacion() {
        List<CanalNotificacion> canales = List.of(
                new AdaptadorCorreo(new ServicioCorreo()),
                new AdaptadorSms(new ServicioSms()),
                new AdaptadorMensajeria(new ServicioMensajeria()));

        for (CanalNotificacion canal : canales) {
            assertTrue(canal.enviar(ana, corto).exitoso(), "Falló el canal " + canal.getTipo());
        }
    }

    @Test
    void elAdaptadorDeCorreoEntregaAsuntoYCuerpoAlServicio() {
        ServicioCorreo servicio = new ServicioCorreo();

        new AdaptadorCorreo(servicio).enviar(ana, corto);

        assertEquals("CORREO a ana@mail.com | Hola | Bienvenida", servicio.getEnviados().get(0));
    }

    @Test
    void elAdaptadorDeSmsUneAsuntoYCuerpo() {
        ServicioSms servicio = new ServicioSms();

        new AdaptadorSms(servicio).enviar(ana, corto);

        assertEquals("SMS a 3001112233 | Hola: Bienvenida", servicio.getEnviados().get(0));
    }

    @Test
    void elAdaptadorDeSmsRecortaLosTextosLargos() {
        ServicioSms servicio = new ServicioSms();
        Mensaje largo = new Mensaje(TipoMensaje.INFORMATIVO, "Aviso", "x".repeat(400));

        ResultadoEnvio resultado = new AdaptadorSms(servicio).enviar(ana, largo);

        assertTrue(resultado.exitoso());
        String enviado = servicio.getEnviados().get(0);
        assertTrue(enviado.endsWith("..."));
        // "SMS a 3001112233 | " tiene 19 caracteres; el texto enviado debe medir 160
        assertEquals(160, enviado.length() - "SMS a 3001112233 | ".length());
    }

    @Test
    void elAdaptadorDeMensajeriaResaltaElAsunto() {
        ServicioMensajeria servicio = new ServicioMensajeria();

        new AdaptadorMensajeria(servicio).enviar(ana, corto);

        assertEquals("CHAT ana_chat | *Hola*\nBienvenida", servicio.getEnviados().get(0));
    }

    @Test
    void sinCorreoElAdaptadorReportaFalloSinLanzarExcepcion() {
        Usuario sinCorreo = new Usuario("Luis", null, "300", "luis", List.of(TipoCanal.CORREO));

        ResultadoEnvio resultado = new AdaptadorCorreo(new ServicioCorreo()).enviar(sinCorreo, corto);

        assertFalse(resultado.exitoso());
        assertEquals(TipoCanal.CORREO, resultado.canal());
    }

    @Test
    void unFalloDelServicioExternoSeConvierteEnResultadoFallido() {
        ResultadoEnvio resultado = new AdaptadorSms(new ServicioSms(1.0)).enviar(ana, corto);

        assertFalse(resultado.exitoso());
        assertTrue(resultado.detalle().contains("SMS"));
    }
}
