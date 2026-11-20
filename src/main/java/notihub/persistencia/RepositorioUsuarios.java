package notihub.persistencia;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;

/**
 * Guarda y lee los usuarios en un archivo CSV (separado por punto y coma).
 * Formato: nombre;correo;telefono;idMensajeria;canales  (canales separados por |,
 * del más al menos preferido, por ejemplo SMS|CORREO).
 */
public class RepositorioUsuarios {

    private static final String SEPARADOR = ";";
    private static final String SEPARADOR_CANALES = "|";
    private static final String ENCABEZADO = "nombre;correo;telefono;idMensajeria;canales";

    private final Path ruta;

    public RepositorioUsuarios(Path ruta) {
        this.ruta = ruta;
    }

    /** Lee los usuarios guardados. Si el archivo no existe, devuelve una lista vacía. */
    public List<Usuario> cargar() {
        if (Files.notExists(ruta)) {
            return List.of();
        }
        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo de usuarios " + ruta, e);
        }
        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 1; i < lineas.size(); i++) { // la línea 0 es el encabezado
            try {
                usuarios.add(deLinea(lineas.get(i)));
            } catch (RuntimeException e) {
                // línea dañada o vacía: se ignora para no perder los demás usuarios
            }
        }
        return usuarios;
    }

    /** Reemplaza el contenido del archivo con la lista de usuarios indicada. */
    public void guardar(List<Usuario> usuarios) {
        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);
        for (Usuario u : usuarios) {
            lineas.add(aLinea(u));
        }
        try {
            Path carpeta = ruta.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo de usuarios " + ruta, e);
        }
    }

    private static String aLinea(Usuario u) {
        List<String> canales = new ArrayList<>();
        for (TipoCanal canal : u.getCanalesPreferidos()) {
            canales.add(canal.name());
        }
        return String.join(SEPARADOR,
                limpiar(u.getNombre()),
                limpiar(u.getCorreo()),
                limpiar(u.getTelefono()),
                limpiar(u.getIdMensajeria()),
                String.join(SEPARADOR_CANALES, canales));
    }

    private static Usuario deLinea(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != 5) {
            throw new IllegalArgumentException("Línea con formato inesperado");
        }
        List<TipoCanal> canales = new ArrayList<>();
        for (String nombre : campos[4].split("\\" + SEPARADOR_CANALES)) {
            if (!nombre.isBlank()) {
                canales.add(TipoCanal.valueOf(nombre.trim()));
            }
        }
        return new Usuario(campos[0], vacioANulo(campos[1]), vacioANulo(campos[2]),
                vacioANulo(campos[3]), canales);
    }

    private static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(SEPARADOR, ",").replace("\r", " ").replace("\n", " ");
    }

    private static String vacioANulo(String texto) {
        return texto.isBlank() ? null : texto;
    }
}
