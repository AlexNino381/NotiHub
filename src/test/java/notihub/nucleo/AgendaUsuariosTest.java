package notihub.nucleo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;
import notihub.persistencia.RepositorioUsuarios;
import org.junit.jupiter.api.Test;

class AgendaUsuariosTest {

    private static Path archivoNuevo() throws IOException {
        Path carpeta = Files.createTempDirectory("notihub-test");
        carpeta.toFile().deleteOnExit();
        return carpeta.resolve("usuarios.csv");
    }

    private static Usuario ana() {
        return new Usuario("Ana", "ana@mail.com", "300", "ana_chat", List.of(TipoCanal.SMS));
    }

    @Test
    void empiezaVaciaSiNoHayArchivo() throws IOException {
        AgendaUsuarios agenda = new AgendaUsuarios(new RepositorioUsuarios(archivoNuevo()));

        assertTrue(agenda.listar().isEmpty());
    }

    @Test
    void losUsuariosAgregadosSobrevivenAUnReinicio() throws IOException {
        Path ruta = archivoNuevo();
        new AgendaUsuarios(new RepositorioUsuarios(ruta)).agregar(ana());

        AgendaUsuarios reiniciada = new AgendaUsuarios(new RepositorioUsuarios(ruta));

        assertEquals(1, reiniciada.listar().size());
        assertTrue(reiniciada.buscar("Ana").isPresent());
    }

    @Test
    void noPermiteNombresRepetidos() throws IOException {
        AgendaUsuarios agenda = new AgendaUsuarios(new RepositorioUsuarios(archivoNuevo()));
        agenda.agregar(ana());

        assertThrows(IllegalArgumentException.class,
                () -> agenda.agregar(new Usuario("ANA", null, null, null, List.of())));
    }

    @Test
    void buscaSinDistinguirMayusculas() throws IOException {
        AgendaUsuarios agenda = new AgendaUsuarios(new RepositorioUsuarios(archivoNuevo()));
        agenda.agregar(ana());

        assertTrue(agenda.buscar("aNa").isPresent());
        assertTrue(agenda.buscar("Pedro").isEmpty());
    }

    @Test
    void eliminaYGuardaElCambio() throws IOException {
        Path ruta = archivoNuevo();
        AgendaUsuarios agenda = new AgendaUsuarios(new RepositorioUsuarios(ruta));
        agenda.agregar(ana());

        assertTrue(agenda.eliminar("Ana"));
        assertFalse(agenda.eliminar("Ana"));
        assertTrue(new AgendaUsuarios(new RepositorioUsuarios(ruta)).listar().isEmpty());
    }
}
