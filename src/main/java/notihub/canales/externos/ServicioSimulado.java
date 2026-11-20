package notihub.canales.externos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Base común de los servicios externos simulados. Permite configurar una
 * probabilidad de fallo (0.0 nunca falla, 1.0 siempre falla), que se puede
 * cambiar en cualquier momento, y guarda lo enviado.
 *
 * Los servicios que extienden esta clase representan sistemas de terceros: no se
 * deben modificar para adaptarse al resto de NotiHub. De eso se encarga el Adapter.
 */
public abstract class ServicioSimulado {

    private final Random azar = new Random();
    private final List<String> enviados = new ArrayList<>();
    private double probabilidadFallo;

    protected ServicioSimulado(double probabilidadFallo) {
        validar(probabilidadFallo);
        this.probabilidadFallo = probabilidadFallo;
    }

    public double getProbabilidadFallo() {
        return probabilidadFallo;
    }

    public void setProbabilidadFallo(double probabilidadFallo) {
        validar(probabilidadFallo);
        this.probabilidadFallo = probabilidadFallo;
    }

    private static void validar(double probabilidad) {
        if (Double.isNaN(probabilidad) || probabilidad < 0.0 || probabilidad > 1.0) {
            throw new IllegalArgumentException("La probabilidad de fallo debe estar entre 0.0 y 1.0");
        }
    }

    /** Lanza una excepción según la probabilidad de fallo configurada. */
    protected void simularPosibleFallo(String nombreServicio) {
        if (azar.nextDouble() < probabilidadFallo) {
            throw new ServicioExternoException("El servicio de " + nombreServicio + " no está disponible");
        }
    }

    protected void registrar(String envio) {
        enviados.add(envio);
    }

    /** Copia de lo que este servicio ha enviado, útil para pruebas y demostraciones. */
    public List<String> getEnviados() {
        return Collections.unmodifiableList(enviados);
    }
}
