package notihub.canales.externos;

/**
 * Servicio de mensajería instantánea simulado. Su interfaz es propia: identificador
 * de chat y texto corto de máximo 280 caracteres.
 */
public class ServicioMensajeria extends ServicioSimulado {

    public static final int LIMITE_CARACTERES = 280;

    public ServicioMensajeria() {
        this(0.0);
    }

    public ServicioMensajeria(double probabilidadFallo) {
        super(probabilidadFallo);
    }

    public void publicarMensaje(String idChat, String texto) {
        if (idChat == null || idChat.isBlank()) {
            throw new ServicioExternoException("Identificador de chat inválido");
        }
        if (texto == null || texto.length() > LIMITE_CARACTERES) {
            throw new ServicioExternoException("El mensaje supera los " + LIMITE_CARACTERES + " caracteres");
        }
        simularPosibleFallo("mensajería");
        registrar("CHAT " + idChat + " | " + texto);
    }
}
