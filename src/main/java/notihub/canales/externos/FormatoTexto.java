package notihub.canales;

/**
 * Utilidad interna de los adaptadores para ajustar textos a los límites de cada canal.
 */
final class FormatoTexto {

    private static final String PUNTOS = "...";

    private FormatoTexto() {
    }

    /** Recorta el texto a {@code maximo} caracteres, terminando en "..." si hubo recorte. */
    static String recortar(String texto, int maximo) {
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo - PUNTOS.length()) + PUNTOS;
    }

    static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
