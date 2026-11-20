package notihub.interfaz.consola;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import notihub.modelo.TipoCanal;
import notihub.nucleo.Sistema;
import org.junit.jupiter.api.Test;

class MenuConsolaTest {

    private Sistema sistema;
    private String salida;

    /** Ejecuta el menú con las líneas indicadas como si el usuario las escribiera. */
    private void ejecutar(String... lineas) throws IOException {
        Path carpeta = Files.createTempDirectory("notihub-test");
        carpeta.toFile().deleteOnExit();
        sistema = Sistema.iniciar(carpeta, 0.0);

        String texto = String.join("\n", lineas) + "\n";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new MenuConsola(sistema,
                new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(buffer, true, StandardCharsets.UTF_8)).ejecutar();
        salida = buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void terminaCuandoSeAcabaLaEntrada() throws IOException {
        ejecutar();

        assertTrue(salida.contains("Hasta luego."));
    }

    @Test
    void listaLosUsuariosDeEjemplo() throws IOException {
        ejecutar("1", "0");

        assertTrue(salida.contains("- Ana"));
        assertTrue(salida.contains("- Luis"));
    }

    @Test
    void registraUnUsuarioNuevo() throws IOException {
        ejecutar("2", "Maria", "maria@mail.com", "3009998877", "maria_chat", "correo, sms", "0");

        assertTrue(salida.contains("Usuario registrado: Maria"));
        assertEquals(List.of(TipoCanal.CORREO, TipoCanal.SMS),
                sistema.getAgenda().buscar("Maria").orElseThrow().getCanalesPreferidos());
    }

    @Test
    void rechazaCanalesQueNoExisten() throws IOException {
        ejecutar("2", "Pedro", "", "", "", "PALOMA", "0");

        assertTrue(salida.contains("Canal no válido"));
        assertTrue(sistema.getAgenda().buscar("Pedro").isEmpty());
    }

    @Test
    void enviaUnMensajeUrgenteYLoRegistraEnElHistorial() throws IOException {
        // 3 = enviar; usuario 1 (Ana); plantilla 3 (alerta); tipo 1 (urgente)
        ejecutar("3", "1", "3", "1", "0");

        assertTrue(salida.contains("Resumen: 3 exitoso(s), 0 fallido(s)."));
        assertEquals(3, sistema.getHistorial().todos().size());
    }

    @Test
    void conFalloTotalTodosLosEnviosFallan() throws IOException {
        // 6 = cambiar probabilidad a 1; luego enviar urgente a Ana
        ejecutar("6", "1", "3", "1", "3", "1", "0");

        assertTrue(salida.contains("Resumen: 0 exitoso(s), 3 fallido(s)."));
        assertTrue(salida.contains("[FALLO]"));
    }

    @Test
    void elHistorialSePuedeFiltrarPorUsuario() throws IOException {
        // Envía a Ana (3 registros) y consulta el historial solo de Luis
        ejecutar("3", "1", "3", "1", "5", "Luis", "", "", "0");

        assertTrue(salida.contains("No hay registros."));
    }

    @Test
    void muestraElHistorialConSusDatos() throws IOException {
        ejecutar("3", "1", "3", "1", "5", "Ana", "SMS", "E", "0");

        assertTrue(salida.contains(" | Ana | SMS | ENVIADO | 1 | "));
    }

    @Test
    void avisaCuandoLaOpcionNoExiste() throws IOException {
        ejecutar("9", "0");

        assertTrue(salida.contains("Opción no válida."));
    }
}
