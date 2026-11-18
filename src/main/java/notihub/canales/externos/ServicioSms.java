package notihub.canales.externos;

/**
 * Servicio de SMS simulado. Su interfaz es propia: número y texto de máximo 160 caracteres.
 */
public class ServicioSms extends ServicioSimulado {

    public static final int LIMITE_CARACTERES = 160;

    public ServicioSms() {
        this(0.0);
    }

    public ServicioSms(double probabilidadFallo) {
        super(probabilidadFallo);
    }

    public void enviarSms(String numero, String texto) {
        if (numero == null || numero.isBlank()) {
            throw new ServicioExternoException("Número de teléfono inválido");
        }
        if (texto == null || texto.length() > LIMITE_CARACTERES) {
            throw new ServicioExternoException("El SMS supera los " + LIMITE_CARACTERES + " caracteres");
        }
        simularPosibleFallo("SMS");
        registrar("SMS a " + numero + " | " + texto);
    }
}
