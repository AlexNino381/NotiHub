package notihub.interfaz.grafica;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import notihub.modelo.TipoCanal;
import notihub.registro.RegistroEnvio;
import org.junit.jupiter.api.Test;

class ModeloTablaHistorialTest {

    @Test
    void muestraUnaFilaPorRegistro() {
        ModeloTablaHistorial modelo = new ModeloTablaHistorial();

        modelo.setRegistros(List.of(
                new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 1, "ok"),
                new RegistroEnvio(LocalDateTime.now(), "Luis", TipoCanal.CORREO, false, 3, "falló")));

        assertEquals(2, modelo.getRowCount());
        assertEquals(6, modelo.getColumnCount());
    }

    @Test
    void presentaLosDatosDeCadaColumna() {
        ModeloTablaHistorial modelo = new ModeloTablaHistorial();
        modelo.setRegistros(List.of(
                new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 2, "ok")));

        assertEquals("Ana", modelo.getValueAt(0, 1));
        assertEquals("SMS", modelo.getValueAt(0, 2));
        assertEquals("ENVIADO", modelo.getValueAt(0, 3));
        assertEquals(2, modelo.getValueAt(0, 4));
    }

    @Test
    void unRegistroSinCanalSeMuestraComoSinCanal() {
        ModeloTablaHistorial modelo = new ModeloTablaHistorial();
        modelo.setRegistros(List.of(
                new RegistroEnvio(LocalDateTime.now(), "Luis", null, false, 0, "no acepta canales")));

        assertEquals("SIN CANAL", modelo.getValueAt(0, 2));
    }
}
