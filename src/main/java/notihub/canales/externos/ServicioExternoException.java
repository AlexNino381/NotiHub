package notihub.canales.externos;

/**
 * Error que lanza un servicio externo simulado cuando no puede completar un envío.
 */
public class ServicioExternoException extends RuntimeException {

    public ServicioExternoException(String mensaje) {
        super(mensaje);
    }
}
