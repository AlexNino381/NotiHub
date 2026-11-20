package notihub.registro;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import notihub.modelo.TipoCanal;

/**
 * PATRÓN OBSERVER - rol "ConcreteObserver".
 * Guarda cada envío como una línea de un archivo CSV (separado por punto y coma),
 * para que el historial sobreviva al cierre de la aplicación.
 */
public class RegistroArchivo implements ObservadorEnvio {

    private static final String SEPARADOR = ";";
    private static final String ENCABEZADO = "fecha;usuario;canal;estado;intentos;detalle";
    private static final String SIN_CANAL = "NINGUNO";

    private final Path ruta;

    public RegistroArchivo(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public void alRegistrarEnvio(RegistroEnvio registro) {
        try {
            prepararArchivo();
            Files.writeString(ruta, aLinea(registro) + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir el historial en " + ruta, e);
        }
    }

    /** Lee todos los registros guardados. Si el archivo no existe, devuelve una lista vacía. */
    public List<RegistroEnvio> cargar() {
        if (Files.notExists(ruta)) {
            return List.of();
        }
        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el historial de " + ruta, e);
        }
        List<RegistroEnvio> registros = new ArrayList<>();
        for (int i = 1; i < lineas.size(); i++) { // la línea 0 es el encabezado
            try {
                registros.add(deLinea(lineas.get(i)));
            } catch (RuntimeException e) {
                // línea dañada o vacía: se ignora para no perder el resto del historial
            }
        }
        return registros;
    }

    private void prepararArchivo() throws IOException {
        Path carpeta = ruta.toAbsolutePath().getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
        if (Files.notExists(ruta)) {
            Files.writeString(ruta, ENCABEZADO + System.lineSeparator(), StandardCharsets.UTF_8);
        }
    }

    private static String aLinea(RegistroEnvio r) {
        return String.join(SEPARADOR,
                r.fecha().toString(),
                limpiar(r.usuario()),
                r.canal() == null ? SIN_CANAL : r.canal().name(),
                r.estado(),
                String.valueOf(r.intentos()),
                limpiar(r.detalle()));
    }

    private static RegistroEnvio deLinea(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != 6) {
            throw new IllegalArgumentException("Línea con formato inesperado");
        }
        TipoCanal canal = campos[2].equals(SIN_CANAL) ? null : TipoCanal.valueOf(campos[2]);
        return new RegistroEnvio(LocalDateTime.parse(campos[0]), campos[1], canal,
                campos[3].equals("ENVIADO"), Integer.parseInt(campos[4]), campos[5]);
    }

    /** Quita del texto lo que rompería el formato: separadores y saltos de línea. */
    private static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(SEPARADOR, ",").replace("\r", " ").replace("\n", " ");
    }
}
