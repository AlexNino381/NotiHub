package notihub.canales.externos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServicioSimuladoTest {

    @Test
    void laProbabilidadDeFalloSePuedeCambiarEnCualquierMomento() {
        ServicioSms sms = new ServicioSms(0.0);
        sms.enviarSms("3001112233", "antes funciona");

        sms.setProbabilidadFallo(1.0);

        assertThrows(ServicioExternoException.class, () -> sms.enviarSms("3001112233", "ahora falla"));
        assertEquals(1, sms.getEnviados().size());
    }

    @Test
    void rechazaProbabilidadesFueraDeRango() {
        ServicioCorreo correo = new ServicioCorreo();

        assertThrows(IllegalArgumentException.class, () -> correo.setProbabilidadFallo(2.0));
        assertThrows(IllegalArgumentException.class, () -> correo.setProbabilidadFallo(-0.5));
    }

    @Test
    void rechazaUnValorQueNoEsNumero() {
        assertThrows(IllegalArgumentException.class, () -> new ServicioCorreo().setProbabilidadFallo(Double.NaN));
    }
}
