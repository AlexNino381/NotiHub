package notihub.registro;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import notihub.modelo.TipoCanal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistorialEnviosTest {

    private HistorialEnvios historial;

    private static RegistroEnvio registro(String usuario, TipoCanal canal, boolean exitoso) {
        return new RegistroEnvio(LocalDateTime.now(), usuario, canal, exitoso, 1, "detalle");
    }

    @BeforeEach
    void preparar() {
        historial = new HistorialEnvios();
        historial.alRegistrarEnvio(registro("Ana", TipoCanal.SMS, true));
        historial.alRegistrarEnvio(registro("Ana", TipoCanal.CORREO, false));
        historial.alRegistrarEnvio(registro("Luis", TipoCanal.CORREO, true));
    }

    @Test
    void sinFiltrosDevuelveTodo() {
        assertEquals(3, historial.consultar(null, null, null).size());
    }

    @Test
    void filtraPorUsuarioSinDistinguirMayusculas() {
        assertEquals(2, historial.consultar("ana", null, null).size());
    }

    @Test
    void filtraPorCanal() {
        assertEquals(2, historial.consultar(null, TipoCanal.CORREO, null).size());
    }

    @Test
    void filtraPorEstado() {
        assertEquals(1, historial.consultar(null, null, false).size());
        assertEquals(2, historial.consultar(null, null, true).size());
    }

    @Test
    void combinaVariosFiltros() {
        assertEquals(1, historial.consultar("Ana", TipoCanal.CORREO, false).size());
    }

    @Test
    void sinCoincidenciasDevuelveListaVacia() {
        assertTrue(historial.consultar("Pedro", null, null).isEmpty());
    }
}
