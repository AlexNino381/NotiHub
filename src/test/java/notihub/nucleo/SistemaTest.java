package notihub.nucleo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.Mensaje;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import org.junit.jupiter.api.Test;

class SistemaTest {

    private static Path carpetaNueva() throws IOException {
        Path carpeta = Files.createTempDirectory("notihub-test");
        carpeta.toFile().deleteOnExit();
        return carpeta;
    }

    @Test
    void laPrimeraVezTraeUsuariosYPlantillasDeEjemplo() throws IOException {
        Sistema sistema = Sistema.iniciar(carpetaNueva(), 0.0);

        assertEquals(2, sistema.getAgenda().listar().size());
        assertEquals(3, sistema.getPlantillas().listar().size());
    }

    @Test
    void cadaEnvioQuedaEnElHistorialYEnElArchivo() throws IOException {
        Path carpeta = carpetaNueva();
        Sistema sistema = Sistema.iniciar(carpeta, 0.0);
        Usuario ana = sistema.getAgenda().buscar("Ana").orElseThrow();

        sistema.getCentral().enviar(ana, new Mensaje(TipoMensaje.URGENTE, "Alerta", "Revisa tu cuenta"));

        assertEquals(3, sistema.getHistorial().todos().size()); // Ana acepta 3 canales
        assertTrue(Files.exists(carpeta.resolve("historial.csv")));
    }

    @Test
    void alReiniciarSeRecuperanUsuariosEHistorial() throws IOException {
        Path carpeta = carpetaNueva();
        Sistema primero = Sistema.iniciar(carpeta, 0.0);
        Usuario luis = primero.getAgenda().buscar("Luis").orElseThrow();
        primero.getCentral().enviar(luis, new Mensaje(TipoMensaje.INFORMATIVO, "Aviso", "Hola"));

        Sistema segundo = Sistema.iniciar(carpeta, 0.0);

        assertEquals(2, segundo.getAgenda().listar().size());
        assertEquals(1, segundo.getHistorial().todos().size());
    }

    @Test
    void conProbabilidadTotalDeFalloTodosLosEnviosFallan() throws IOException {
        Sistema sistema = Sistema.iniciar(carpetaNueva(), 0.0);
        sistema.setProbabilidadFallo(1.0);
        Usuario ana = sistema.getAgenda().buscar("Ana").orElseThrow();

        List<ResultadoEnvio> resultados = sistema.getCentral().enviar(ana,
                new Mensaje(TipoMensaje.URGENTE, "Alerta", "Revisa tu cuenta"));

        assertEquals(3, resultados.size());
        assertTrue(resultados.stream().noneMatch(ResultadoEnvio::exitoso));
        assertEquals(3, resultados.get(0).intentos());
    }
}
