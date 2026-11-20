package notihub.persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;
import org.junit.jupiter.api.Test;

class RepositorioUsuariosTest {

    private static Path archivoNuevo() throws IOException {
        Path carpeta = Files.createTempDirectory("notihub-test");
        carpeta.toFile().deleteOnExit();
        return carpeta.resolve("usuarios.csv");
    }

    @Test
    void guardaYRecuperaLosUsuarios() throws IOException {
        RepositorioUsuarios repo = new RepositorioUsuarios(archivoNuevo());
        Usuario ana = new Usuario("Ana", "ana@mail.com", "3001112233", "ana_chat",
                List.of(TipoCanal.SMS, TipoCanal.CORREO));

        repo.guardar(List.of(ana));
        List<Usuario> cargados = repo.cargar();

        assertEquals(1, cargados.size());
        assertEquals("Ana", cargados.get(0).getNombre());
        assertEquals("ana@mail.com", cargados.get(0).getCorreo());
        assertEquals("3001112233", cargados.get(0).getTelefono());
        assertEquals("ana_chat", cargados.get(0).getIdMensajeria());
    }

    @Test
    void conservaElOrdenDePreferenciaDeLosCanales() throws IOException {
        RepositorioUsuarios repo = new RepositorioUsuarios(archivoNuevo());
        repo.guardar(List.of(new Usuario("Ana", null, null, null,
                List.of(TipoCanal.MENSAJERIA, TipoCanal.SMS, TipoCanal.CORREO))));

        assertEquals(List.of(TipoCanal.MENSAJERIA, TipoCanal.SMS, TipoCanal.CORREO),
                repo.cargar().get(0).getCanalesPreferidos());
    }

    @Test
    void losDatosVaciosVuelvenComoNulos() throws IOException {
        RepositorioUsuarios repo = new RepositorioUsuarios(archivoNuevo());
        repo.guardar(List.of(new Usuario("Luis", "luis@mail.com", null, null, List.of(TipoCanal.CORREO))));

        Usuario luis = repo.cargar().get(0);

        assertNull(luis.getTelefono());
        assertNull(luis.getIdMensajeria());
    }

    @Test
    void unArchivoInexistenteDevuelveListaVacia() throws IOException {
        assertTrue(new RepositorioUsuarios(archivoNuevo()).cargar().isEmpty());
    }

    @Test
    void ignoraLasLineasMalFormadas() throws IOException {
        Path ruta = archivoNuevo();
        Files.writeString(ruta, "nombre;correo;telefono;idMensajeria;canales\n"
                + "basura\n"
                + "Ana;ana@mail.com;300;chat;SMS|CORREO\n"
                + "Pedro;;;;CANAL_INVENTADO\n", StandardCharsets.UTF_8);

        List<Usuario> cargados = new RepositorioUsuarios(ruta).cargar();

        assertEquals(1, cargados.size());
        assertEquals("Ana", cargados.get(0).getNombre());
    }
}
