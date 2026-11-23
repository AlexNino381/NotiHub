package notihub;

import java.nio.file.Path;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import notihub.interfaz.grafica.VentanaPrincipal;
import notihub.nucleo.Sistema;

/**
 * Punto de entrada de la versión con interfaz gráfica. Usa la misma carpeta
 * "datos" y el mismo núcleo que la versión de consola ({@link Principal}).
 */
public class PrincipalGrafico {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si no se puede aplicar el aspecto del sistema, se usa el predeterminado
        }
        Sistema sistema = Sistema.iniciar(Path.of("datos"), 0.0);
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema).setVisible(true));
    }
}
