package notihub.interfaz.grafica;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import javax.swing.JTextArea;
import notihub.modelo.TipoCanal;
import notihub.registro.RegistroEnvio;
import org.junit.jupiter.api.Test;

class RegistroAreaTextoTest {

    @Test
    void escribeCadaEnvioEnElAreaDeTexto() {
        JTextArea area = new JTextArea();
        RegistroAreaTexto observador = new RegistroAreaTexto(area);

        observador.alRegistrarEnvio(new RegistroEnvio(LocalDateTime.now(), "Ana", TipoCanal.SMS, true, 1, "SMS enviado"));
        observador.alRegistrarEnvio(new RegistroEnvio(LocalDateTime.now(), "Luis", TipoCanal.CORREO, false, 3, "fallo"));

        assertTrue(area.getText().contains("[OK]    SMS -> Ana"));
        assertTrue(area.getText().contains("[FALLO] CORREO -> Luis"));
    }
}
