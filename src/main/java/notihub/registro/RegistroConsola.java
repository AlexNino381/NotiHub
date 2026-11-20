package notihub.registro;

import java.io.PrintStream;

/**
 * PATRÓN OBSERVER - rol "ConcreteObserver".
 * Muestra cada envío en la salida indicada (por defecto, la consola).
 */
public class RegistroConsola implements ObservadorEnvio {

    private final PrintStream salida;

    public RegistroConsola() {
        this(System.out);
    }

    public RegistroConsola(PrintStream salida) {
        this.salida = salida;
    }

    @Override
    public void alRegistrarEnvio(RegistroEnvio registro) {
        String canal = registro.canal() == null ? "SIN CANAL" : registro.canal().name();
        salida.println((registro.exitoso() ? "[OK]    " : "[FALLO] ")
                + canal + " -> " + registro.usuario()
                + " (" + registro.intentos() + " intento(s)): " + registro.detalle());
    }
}
