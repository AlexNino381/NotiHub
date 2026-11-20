package notihub.nucleo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import org.junit.jupiter.api.Test;

class ReintentosTest {

    private final Usuario ana = new Usuario("Ana", null, "3001112233", null, List.of(TipoCanal.SMS));
    private final Mensaje mensaje = new Mensaje(TipoMensaje.INFORMATIVO, "Aviso", "Hay novedades");

    @Test
    void unEnvioExitosoALaPrimeraTieneUnIntento() {
        CanalFalso canal = new CanalFalso(TipoCanal.SMS, 0);
        CentralNotificaciones central = new CentralNotificaciones(List.of(canal));

        ResultadoEnvio resultado = central.enviar(ana, mensaje).get(0);

        assertTrue(resultado.exitoso());
        assertEquals(1, resultado.intentos());
        assertEquals(1, canal.getLlamadas());
    }

    @Test
    void reintentaHastaTenerExito() {
        CanalFalso canal = new CanalFalso(TipoCanal.SMS, 2);
        CentralNotificaciones central = new CentralNotificaciones(List.of(canal));

        ResultadoEnvio resultado = central.enviar(ana, mensaje).get(0);

        assertTrue(resultado.exitoso());
        assertEquals(3, resultado.intentos());
        assertEquals(3, canal.getLlamadas());
    }

    @Test
    void seRindeTrasElMaximoDeIntentos() {
        CanalFalso canal = new CanalFalso(TipoCanal.SMS, 10);
        CentralNotificaciones central = new CentralNotificaciones(List.of(canal));

        ResultadoEnvio resultado = central.enviar(ana, mensaje).get(0);

        assertFalse(resultado.exitoso());
        assertEquals(CentralNotificaciones.INTENTOS_POR_DEFECTO, resultado.intentos());
        assertEquals(3, canal.getLlamadas());
    }

    @Test
    void elNumeroDeIntentosEsConfigurable() {
        CanalFalso canal = new CanalFalso(TipoCanal.SMS, 10);
        CentralNotificaciones central = new CentralNotificaciones(List.of(canal), 5);

        ResultadoEnvio resultado = central.enviar(ana, mensaje).get(0);

        assertEquals(5, resultado.intentos());
        assertEquals(5, canal.getLlamadas());
    }

    @Test
    void necesitaAlMenosUnIntento() {
        assertThrows(IllegalArgumentException.class,
                () -> new CentralNotificaciones(List.of(new CanalFalso(TipoCanal.SMS, 0)), 0));
    }
}
