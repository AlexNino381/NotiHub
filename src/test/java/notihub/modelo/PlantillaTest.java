package notihub.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PlantillaTest {

    private final Usuario ana = new Usuario("Ana", "ana@mail.com", "3001112233",
            "ana_chat", List.of(TipoCanal.CORREO));

    @Test
    void reemplazaLosMarcadoresEnAsuntoYCuerpo() {
        Plantilla p = new Plantilla("bienvenida", "Hola {nombre}", "Escribiremos a {correo}");

        Mensaje m = p.generar(TipoMensaje.INFORMATIVO, ana);

        assertEquals("Hola Ana", m.asunto());
        assertEquals("Escribiremos a ana@mail.com", m.cuerpo());
    }

    @Test
    void conservaElTipoDeMensaje() {
        Plantilla p = new Plantilla("aviso", "Aviso", "Cuerpo para {nombre}");

        assertEquals(TipoMensaje.URGENTE, p.generar(TipoMensaje.URGENTE, ana).tipo());
    }

    @Test
    void dejaIntactosLosMarcadoresDesconocidos() {
        Plantilla p = new Plantilla("raro", "Asunto", "Hola {nombre}, tu {codigo}");

        assertEquals("Hola Ana, tu {codigo}", p.generar(TipoMensaje.INFORMATIVO, ana).cuerpo());
    }

    @Test
    void incluyeLaFechaDeHoy() {
        Plantilla p = new Plantilla("fecha", "Asunto", "Hoy es {fecha}");

        assertFalse(p.generar(TipoMensaje.INFORMATIVO, ana).cuerpo().contains("{fecha}"));
    }

    @Test
    void elMensajeNoPuedeTenerCuerpoVacio() {
        Plantilla p = new Plantilla("vacia", "Asunto", "");

        assertThrows(IllegalArgumentException.class,
                () -> p.generar(TipoMensaje.INFORMATIVO, ana));
    }
}
