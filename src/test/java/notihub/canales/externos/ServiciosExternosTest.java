package notihub.canales.externos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServiciosExternosTest {

    @Test
    void elCorreoSeRegistraCuandoTodoEsValido() {
        ServicioCorreo correo = new ServicioCorreo();

        correo.enviarCorreo("ana@mail.com", "Hola", "Cuerpo");

        assertEquals(1, correo.getEnviados().size());
    }

    @Test
    void elCorreoRechazaDireccionesInvalidas() {
        ServicioCorreo correo = new ServicioCorreo();

        assertThrows(ServicioExternoException.class,
                () -> correo.enviarCorreo("sin-arroba", "Hola", "Cuerpo"));
    }

    @Test
    void unServicioConFalloTotalSiempreFalla() {
        ServicioCorreo correo = new ServicioCorreo(1.0);

        assertThrows(ServicioExternoException.class,
                () -> correo.enviarCorreo("ana@mail.com", "Hola", "Cuerpo"));
        assertTrue(correo.getEnviados().isEmpty());
    }

    @Test
    void elSmsAceptaExactamente160Caracteres() {
        ServicioSms sms = new ServicioSms();

        sms.enviarSms("3001112233", "a".repeat(160));

        assertEquals(1, sms.getEnviados().size());
    }

    @Test
    void elSmsRechazaMasDe160Caracteres() {
        ServicioSms sms = new ServicioSms();

        assertThrows(ServicioExternoException.class,
                () -> sms.enviarSms("3001112233", "a".repeat(161)));
    }

    @Test
    void laMensajeriaRechazaTextosMuyLargos() {
        ServicioMensajeria chat = new ServicioMensajeria();

        assertThrows(ServicioExternoException.class,
                () -> chat.publicarMensaje("ana_chat", "a".repeat(281)));
    }

    @Test
    void laMensajeriaRechazaChatVacio() {
        ServicioMensajeria chat = new ServicioMensajeria();

        assertThrows(ServicioExternoException.class,
                () -> chat.publicarMensaje(" ", "Hola"));
    }

    @Test
    void laProbabilidadDeFalloDebeEstarEntreCeroYUno() {
        assertThrows(IllegalArgumentException.class, () -> new ServicioSms(1.5));
        assertThrows(IllegalArgumentException.class, () -> new ServicioSms(-0.1));
    }
}
