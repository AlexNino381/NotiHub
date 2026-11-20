package notihub.nucleo;

import static org.junit.jupiter.api.Assertions.*;

import notihub.modelo.Plantilla;
import org.junit.jupiter.api.Test;

class CatalogoPlantillasTest {

    @Test
    void traePlantillasDeEjemplo() {
        assertEquals(3, new CatalogoPlantillas().listar().size());
    }

    @Test
    void permiteCrearYBuscarPlantillas() {
        CatalogoPlantillas catalogo = new CatalogoPlantillas();

        catalogo.agregar(new Plantilla("promo", "Oferta", "Hola {nombre}, tenemos una oferta"));

        assertEquals(4, catalogo.listar().size());
        assertTrue(catalogo.buscar("PROMO").isPresent());
    }

    @Test
    void noPermiteNombresRepetidos() {
        CatalogoPlantillas catalogo = new CatalogoPlantillas();

        assertThrows(IllegalArgumentException.class,
                () -> catalogo.agregar(new Plantilla("Alerta", "otro", "otro")));
    }

    @Test
    void unaPlantillaInexistenteNoSeEncuentra() {
        assertTrue(new CatalogoPlantillas().buscar("no-existe").isEmpty());
    }
}
