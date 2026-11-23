package notihub.interfaz.grafica;

import javax.swing.JFrame;
import notihub.nucleo.Sistema;

/**
 * Ventana de la versión gráfica. Solo aloja el panel; usa el mismo núcleo
 * ({@link Sistema}) que la versión de consola.
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(Sistema sistema) {
        super("NotiHub - Notificaciones multicanal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new PanelNotiHub(sistema));
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }
}
