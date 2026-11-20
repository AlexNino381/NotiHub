package notihub;

import java.nio.file.Path;
import notihub.interfaz.consola.MenuConsola;
import notihub.nucleo.Sistema;

/**
 * Punto de entrada de la versión de consola. Los datos se guardan en la carpeta
 * "datos" del directorio de trabajo.
 *
 * Uso opcional: un primer argumento con la probabilidad inicial de fallo de los
 * canales simulados (0.0 a 1.0). Por defecto, 0.0.
 */
public class Principal {

    public static void main(String[] args) {
        double probabilidadFallo = 0.0;
        if (args.length > 0) {
            try {
                probabilidadFallo = Double.parseDouble(args[0].replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Probabilidad no válida, se usará 0.0");
            }
        }
        Sistema sistema = Sistema.iniciar(Path.of("datos"), probabilidadFallo);
        new MenuConsola(sistema, System.in, System.out).ejecutar();
    }
}
