package notihub.registro;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import notihub.modelo.TipoCanal;
import org.junit.jupiter.api.Test;

class RegistroArchivoTest {

    private static Path archivoNuevo() throws IOException {
        Path carpeta = Files.createTempDirectory("notihub-test");
        carpeta.toFile().deleteOnExit();
        return carpeta.resolve("historial.csv");
    }

    @Test
    void guardaYRecuperaLosRegistros() throws IOException {
        Path ruta = archivoNuevo();
        RegistroEnvio exitoso = new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 2, "SMS enviado");
        RegistroEnvio sinCanal = new RegistroEnvio(LocalDateTime.now(), "Luis", null, false, 0, "No acepta canales");
        RegistroArchivo archivo = new RegistroArchivo(ruta);

        archivo.alRegistrarEnvio(exitoso);
        archivo.alRegistrarEnvio(sinCanal);

        assertEquals(List.of(exitoso, sinCanal), new RegistroArchivo(ruta).cargar());
    }

    @Test
    void agregaAlFinalSinBorrarLoAnterior() throws IOException {
        Path ruta = archivoNuevo();
        new RegistroArchivo(ruta).alRegistrarEnvio(
                new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 1, "uno"));
        new RegistroArchivo(ruta).alRegistrarEnvio(
                new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.CORREO, true, 1, "dos"));

        assertEquals(2, new RegistroArchivo(ruta).cargar().size());
    }

    @Test
    void cargarUnArchivoInexistenteDevuelveListaVacia() throws IOException {
        assertTrue(new RegistroArchivo(archivoNuevo()).cargar().isEmpty());
    }

    @Test
    void limpiaSeparadoresYSaltosDeLineaDelDetalle() throws IOException {
        Path ruta = archivoNuevo();
        new RegistroArchivo(ruta).alRegistrarEnvio(
                new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 1, "a;b\nc"));

        assertEquals("a,b c", new RegistroArchivo(ruta).cargar().get(0).detalle());
    }

    @Test
    void ignoraLasLineasMalFormadas() throws IOException {
        Path ruta = archivoNuevo();
        String contenido = "fecha;usuario;canal;estado;intentos;detalle\n"
                + "esto no es un registro\n"
                + "2026-11-18T10:00:00;Ana;SMS;ENVIADO;1;ok\n";
        Files.writeString(ruta, contenido, StandardCharsets.UTF_8);

        List<RegistroEnvio> registros = new RegistroArchivo(ruta).cargar();

        assertEquals(1, registros.size());
        assertEquals("Ana", registros.get(0).usuario());
    }
}
