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
import notihub.registro.HistorialEnvios;
import notihub.registro.RegistroEnvio;
import org.junit.jupiter.api.Test;

class ObservadoresTest {

    private final Mensaje urgente = new Mensaje(TipoMensaje.URGENTE, "Alerta", "Revisa tu cuenta");

    private CentralNotificaciones centralConTodosLosCanales() {
        return new CentralNotificaciones(List.of(
                new AdaptadorCorreo(new ServicioCorreo()),
                new AdaptadorSms(new ServicioSms()),
                new AdaptadorMensajeria(new ServicioMensajeria())));
    }

    @Test
    void notificaUnRegistroPorCadaCanalUsado() {
        CentralNotificaciones central = centralConTodosLosCanales();
        HistorialEnvios historial = new HistorialEnvios();
        central.agregarObservador(historial);
        Usuario ana = new Usuario("Ana", "ana@mail.com", "3001112233", "ana_chat",
                List.of(TipoCanal.SMS, TipoCanal.CORREO));

        central.enviar(ana, urgente);

        assertEquals(2, historial.todos().size());
        assertEquals("Ana", historial.todos().get(0).usuario());
    }

    @Test
    void variosObservadoresRecibenLosMismosEnvios() {
        CentralNotificaciones central = centralConTodosLosCanales();
        HistorialEnvios uno = new HistorialEnvios();
        HistorialEnvios otro = new HistorialEnvios();
        central.agregarObservador(uno);
        central.agregarObservador(otro);
        Usuario ana = new Usuario("Ana", "ana@mail.com", null, null, List.of(TipoCanal.CORREO));

        central.enviar(ana, urgente);

        assertEquals(1, uno.todos().size());
        assertEquals(1, otro.todos().size());
    }

    @Test
    void elRegistroIncluyeLosIntentosRealizados() {
        CentralNotificaciones central = new CentralNotificaciones(List.of(new CanalFalso(TipoCanal.SMS, 1)));
        HistorialEnvios historial = new HistorialEnvios();
        central.agregarObservador(historial);
        Usuario ana = new Usuario("Ana", null, "300", null, List.of(TipoCanal.SMS));

        central.enviar(ana, urgente);

        RegistroEnvio registro = historial.todos().get(0);
        assertTrue(registro.exitoso());
        assertEquals(2, registro.intentos());
    }

    @Test
    void tambienRegistraLosEnviosSinCanalDisponible() {
        CentralNotificaciones central = centralConTodosLosCanales();
        HistorialEnvios historial = new HistorialEnvios();
        central.agregarObservador(historial);
        Usuario sinCanales = new Usuario("Luis", null, null, null, List.of());

        central.enviar(sinCanales, urgente);

        assertEquals(1, historial.todos().size());
        assertNull(historial.todos().get(0).canal());
        assertFalse(historial.todos().get(0).exitoso());
    }

    @Test
    void unObservadorQueFallaNoDetieneLosEnvios() {
        CentralNotificaciones central = centralConTodosLosCanales();
        HistorialEnvios historial = new HistorialEnvios();
        central.agregarObservador(registro -> {
            throw new IllegalStateException("observador roto");
        });
        central.agregarObservador(historial);
        Usuario ana = new Usuario("Ana", "ana@mail.com", null, null, List.of(TipoCanal.CORREO));

        List<ResultadoEnvio> resultados = central.enviar(ana, urgente);

        assertTrue(resultados.get(0).exitoso());
        assertEquals(1, historial.todos().size());
    }
}
