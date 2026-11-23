package notihub.interfaz.grafica;

import javax.swing.JTextArea;
import notihub.registro.ObservadorEnvio;
import notihub.registro.RegistroEnvio;

/**
 * PATRÓN OBSERVER - rol "ConcreteObserver" propio de la interfaz gráfica.
 * Escribe cada envío en un área de texto de la ventana. El núcleo no sabe que
 * existe: solo conoce la interfaz {@link ObservadorEnvio}.
 *
 * Los envíos los inicia un botón, así que esto se ejecuta en el hilo de la interfaz.
 */
public class RegistroAreaTexto implements ObservadorEnvio {

    private final JTextArea area;

    public RegistroAreaTexto(JTextArea area) {
        this.area = area;
    }

    @Override
    public void alRegistrarEnvio(RegistroEnvio registro) {
        String canal = registro.canal() == null ? "SIN CANAL" : registro.canal().name();
        area.append((registro.exitoso() ? "[OK]    " : "[FALLO] ")
                + canal + " -> " + registro.usuario()
                + " (" + registro.intentos() + " intento(s)): " + registro.detalle() + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }
}
