package notihub.registro;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import notihub.modelo.TipoCanal;
import org.junit.jupiter.api.Test;

class RegistroConsolaTest {

    private String capturar(RegistroEnvio registro) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new RegistroConsola(new PrintStream(buffer, true, StandardCharsets.UTF_8)).alRegistrarEnvio(registro);
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void muestraLosEnviosExitosos() {
        String texto = capturar(new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 1, "SMS enviado"));

        assertTrue(texto.contains("[OK]"));
        assertTrue(texto.contains("Ana"));
    }

    @Test
    void muestraLosFallosSinCanal() {
        String texto = capturar(new RegistroEnvio(LocalDateTime.now(), "Luis", null, false, 0, "No acepta canales"));

        assertTrue(texto.contains("[FALLO]"));
        assertTrue(texto.contains("SIN CANAL"));
    }
}
