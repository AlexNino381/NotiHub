package notihub.canales.externos;

/**
 * Servicio de correo simulado. Su interfaz es propia: dirección, asunto y cuerpo.
 */
public class ServicioCorreo extends ServicioSimulado {

    public ServicioCorreo() {
        this(0.0);
    }

    public ServicioCorreo(double probabilidadFallo) {
        super(probabilidadFallo);
    }

    public void enviarCorreo(String direccion, String asunto, String cuerpo) {
        if (direccion == null || !direccion.contains("@")) {
            throw new ServicioExternoException("Dirección de correo inválida: " + direccion);
        }
        simularPosibleFallo("correo");
        registrar("CORREO a " + direccion + " | " + asunto + " | " + cuerpo);
    }
}
