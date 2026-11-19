package notihub.nucleo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import notihub.canales.AdaptadorCorreo;
import notihub.canales.AdaptadorMensajeria;
import notihub.canales.AdaptadorSms;
import notihub.canales.ResultadoEnvio;
import notihub.canales.externos.ServicioCorreo;
import notihub.canales.externos.ServicioMensajeria;
import notihub.canales.externos.ServicioSms;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CentralNotificacionesTest {

    private ServicioCorreo correo;
    private ServicioSms sms;
    private ServicioMensajeria chat;
    private CentralNotificaciones central;

    private final Mensaje urgente = new Mensaje(TipoMensaje.URGENTE, "Alerta", "Revisa tu cuenta");
    private final Mensaje informativo = new Mensaje(TipoMensaje.INFORMATIVO, "Aviso", "Hay novedades");

    @BeforeEach
    void preparar() {
        correo = new ServicioCorreo();
        sms = new ServicioSms();
        chat = new ServicioMensajeria();
        central = new CentralNotificaciones(List.of(
                new AdaptadorCorreo(correo), new AdaptadorSms(sms), new AdaptadorMensajeria(chat)));
    }

    private Usuario usuario(TipoCanal... canales) {
        return new Usuario("Ana", "ana@mail.com", "3001112233", "ana_chat", List.of(canales));
    }

    @Test
    void unMensajeUrgenteSalePorTodosLosCanalesQueElUsuarioAcepta() {
        List<ResultadoEnvio> resultados = central.enviar(
                usuario(TipoCanal.SMS, TipoCanal.CORREO), urgente);

        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().allMatch(ResultadoEnvio::exitoso));
        assertEquals(1, sms.getEnviados().size());
        assertEquals(1, correo.getEnviados().size());
        assertTrue(chat.getEnviados().isEmpty());
    }

    @Test
    void unMensajeInformativoSaleSoloPorElCanalPrincipal() {
        List<ResultadoEnvio> resultados = central.enviar(
                usuario(TipoCanal.SMS, TipoCanal.CORREO), informativo);

        assertEquals(1, resultados.size());
        assertEquals(TipoCanal.SMS, resultados.get(0).canal());
        assertTrue(correo.getEnviados().isEmpty());
    }

    @Test
    void nuncaSeEnviaPorUnCanalQueElUsuarioNoAcepta() {
        central.enviar(usuario(TipoCanal.CORREO), urgente);

        assertTrue(sms.getEnviados().isEmpty());
        assertTrue(chat.getEnviados().isEmpty());
    }

    @Test
    void siElUsuarioNoAceptaNingunCanalSeReportaElFallo() {
        List<ResultadoEnvio> resultados = central.enviar(usuario(), urgente);

        assertEquals(1, resultados.size());
        assertFalse(resultados.get(0).exitoso());
        assertNull(resultados.get(0).canal());
    }

    @Test
    void elFalloDeUnCanalNoDetieneLosDemas() {
        CentralNotificaciones conCorreoCaido = new CentralNotificaciones(List.of(
                new AdaptadorCorreo(new ServicioCorreo(1.0)), new AdaptadorSms(sms)));

        List<ResultadoEnvio> resultados = conCorreoCaido.enviar(
                usuario(TipoCanal.CORREO, TipoCanal.SMS), urgente);

        assertEquals(2, resultados.size());
        assertFalse(resultados.get(0).exitoso());
        assertTrue(resultados.get(1).exitoso());
        assertEquals(1, sms.getEnviados().size());
    }

    @Test
    void necesitaAlMenosUnCanalDisponible() {
        assertThrows(IllegalArgumentException.class, () -> new CentralNotificaciones(List.of()));
    }
}
