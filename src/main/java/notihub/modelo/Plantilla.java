package notihub.modelo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de mensaje con marcadores entre llaves, por ejemplo {nombre}.
 * Marcadores disponibles: {nombre}, {correo}, {telefono} y {fecha}.
 */
public class Plantilla {

    private final String nombre;
    private final String asuntoModelo;
    private final String cuerpoModelo;

    public Plantilla(String nombre, String asuntoModelo, String cuerpoModelo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la plantilla es obligatorio");
        }
        this.nombre = nombre;
        this.asuntoModelo = asuntoModelo == null ? "" : asuntoModelo;
        this.cuerpoModelo = cuerpoModelo == null ? "" : cuerpoModelo;
    }

    public String getNombre() {
        return nombre;
    }

    /** Crea un mensaje personalizado para el usuario indicado. */
    public Mensaje generar(TipoMensaje tipo, Usuario usuario) {
        Map<String, String> valores = new HashMap<>();
        valores.put("nombre", valorSeguro(usuario.getNombre()));
        valores.put("correo", valorSeguro(usuario.getCorreo()));
        valores.put("telefono", valorSeguro(usuario.getTelefono()));
        valores.put("fecha", LocalDate.now().toString());

        return new Mensaje(tipo,
                reemplazar(asuntoModelo, valores),
                reemplazar(cuerpoModelo, valores));
    }

    private static String reemplazar(String texto, Map<String, String> valores) {
        String resultado = texto;
        for (Map.Entry<String, String> par : valores.entrySet()) {
            resultado = resultado.replace("{" + par.getKey() + "}", par.getValue());
        }
        return resultado;
    }

    private static String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }
}
