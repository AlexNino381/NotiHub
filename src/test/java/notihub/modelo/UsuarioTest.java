package notihub.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void elNombreEsObligatorio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario(" ", "a@b.com", "300", "chat1", List.of(TipoCanal.CORREO)));
    }

    @Test
    void conservaElOrdenDePreferencia() {
        Usuario u = new Usuario("Ana", "ana@mail.com", "300", "ana_chat",
                List.of(TipoCanal.SMS, TipoCanal.CORREO));

        assertEquals(List.of(TipoCanal.SMS, TipoCanal.CORREO), u.getCanalesPreferidos());
        assertEquals(TipoCanal.SMS, u.getCanalPrincipal().orElseThrow());
    }

    @Test
    void eliminaCanalesRepetidos() {
        Usuario u = new Usuario("Ana", null, null, null,
                List.of(TipoCanal.SMS, TipoCanal.SMS, TipoCanal.CORREO));

        assertEquals(2, u.getCanalesPreferidos().size());
    }

    @Test
    void aceptaSoloLosCanalesElegidos() {
        Usuario u = new Usuario("Ana", null, null, null, List.of(TipoCanal.CORREO));

        assertTrue(u.acepta(TipoCanal.CORREO));
        assertFalse(u.acepta(TipoCanal.SMS));
    }

    @Test
    void sinCanalesNoHayCanalPrincipal() {
        Usuario u = new Usuario("Ana", null, null, null, List.of());

        assertTrue(u.getCanalPrincipal().isEmpty());
    }

    @Test
    void laListaDePreferenciasNoSePuedeModificarDesdeFuera() {
        Usuario u = new Usuario("Ana", null, null, null, List.of(TipoCanal.CORREO));

        assertThrows(UnsupportedOperationException.class,
                () -> u.getCanalesPreferidos().add(TipoCanal.SMS));
    }
}
