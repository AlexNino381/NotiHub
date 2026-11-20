package notihub.interfaz.consola;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.Mensaje;
import notihub.modelo.Plantilla;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import notihub.nucleo.Sistema;
import notihub.registro.RegistroConsola;
import notihub.registro.RegistroEnvio;

/**
 * Interfaz de consola. No contiene lógica de negocio: solo pide datos, llama al
 * núcleo ({@link Sistema}) y muestra lo que este responde.
 *
 * Se suscribe como observador del núcleo con un {@link RegistroConsola}, así cada
 * envío se muestra en pantalla apenas ocurre.
 */
public class MenuConsola {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Sistema sistema;
    private final Scanner entrada;
    private final PrintStream salida;

    public MenuConsola(Sistema sistema, InputStream entrada, PrintStream salida) {
        this.sistema = sistema;
        this.entrada = new Scanner(entrada, StandardCharsets.UTF_8);
        this.salida = salida;
        sistema.getCentral().agregarObservador(new RegistroConsola(salida));
    }

    public void ejecutar() {
        salida.println("=== NotiHub - Notificaciones multicanal ===");
        boolean seguir = true;
        while (seguir) {
            mostrarMenu();
            String opcion = leer("Opción: ");
            if (opcion == null) { // se acabó la entrada
                break;
            }
            switch (opcion.trim()) {
                case "1" -> listarUsuarios();
                case "2" -> registrarUsuario();
                case "3" -> enviarMensaje();
                case "4" -> crearPlantilla();
                case "5" -> verHistorial();
                case "6" -> cambiarProbabilidadDeFallo();
                case "0" -> seguir = false;
                default -> salida.println("Opción no válida.");
            }
        }
        salida.println("Hasta luego.");
    }

    private void mostrarMenu() {
        salida.println();
        salida.println("1) Ver usuarios");
        salida.println("2) Registrar usuario");
        salida.println("3) Enviar mensaje");
        salida.println("4) Crear plantilla");
        salida.println("5) Ver historial de envíos");
        salida.println("6) Cambiar probabilidad de fallo de los canales (actual: "
                + sistema.getProbabilidadFallo() + ")");
        salida.println("0) Salir");
    }

    // ---------- opciones del menú ----------

    private void listarUsuarios() {
        List<Usuario> usuarios = sistema.getAgenda().listar();
        if (usuarios.isEmpty()) {
            salida.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : usuarios) {
            salida.println("- " + u.getNombre() + " | correo: " + valor(u.getCorreo())
                    + " | teléfono: " + valor(u.getTelefono())
                    + " | chat: " + valor(u.getIdMensajeria())
                    + " | canales: " + u.getCanalesPreferidos());
        }
    }

    private void registrarUsuario() {
        String nombre = leer("Nombre: ");
        String correo = leer("Correo (Enter si no tiene): ");
        String telefono = leer("Teléfono (Enter si no tiene): ");
        String chat = leer("Identificador de mensajería (Enter si no tiene): ");
        String canales = leer("Canales en orden de preferencia, separados por coma (CORREO,SMS,MENSAJERIA): ");
        if (nombre == null || correo == null || telefono == null || chat == null || canales == null) {
            return;
        }
        Optional<List<TipoCanal>> preferidos = interpretarCanales(canales);
        if (preferidos.isEmpty()) {
            salida.println("Canal no válido. Usa solo CORREO, SMS o MENSAJERIA. No se registró el usuario.");
            return;
        }
        try {
            sistema.getAgenda().agregar(new Usuario(nombre, textoONulo(correo), textoONulo(telefono),
                    textoONulo(chat), preferidos.get()));
            salida.println("Usuario registrado: " + nombre.trim());
        } catch (IllegalArgumentException e) {
            salida.println("No se pudo registrar: " + e.getMessage());
        }
    }

    private void enviarMensaje() {
        Usuario usuario = elegir("Usuario", sistema.getAgenda().listar(), Usuario::getNombre);
        if (usuario == null) {
            return;
        }
        Plantilla plantilla = elegir("Plantilla", sistema.getPlantillas().listar(), Plantilla::getNombre);
        if (plantilla == null) {
            return;
        }
        TipoMensaje tipo = elegir("Tipo de mensaje", List.of(TipoMensaje.URGENTE, TipoMensaje.INFORMATIVO),
                TipoMensaje::name);
        if (tipo == null) {
            return;
        }
        try {
            Mensaje mensaje = plantilla.generar(tipo, usuario);
            List<ResultadoEnvio> resultados = sistema.getCentral().enviar(usuario, mensaje);
            long exitosos = resultados.stream().filter(ResultadoEnvio::exitoso).count();
            salida.println("Resumen: " + exitosos + " exitoso(s), " + (resultados.size() - exitosos) + " fallido(s).");
        } catch (IllegalArgumentException e) {
            salida.println("No se pudo preparar el mensaje: " + e.getMessage());
        }
    }

    private void crearPlantilla() {
        String nombre = leer("Nombre de la plantilla: ");
        String asunto = leer("Asunto (puede usar {nombre}, {fecha}): ");
        String cuerpo = leer("Cuerpo (puede usar {nombre}, {correo}, {telefono}, {fecha}): ");
        if (nombre == null || asunto == null || cuerpo == null) {
            return;
        }
        if (cuerpo.isBlank()) {
            salida.println("El cuerpo no puede estar vacío. No se creó la plantilla.");
            return;
        }
        try {
            sistema.getPlantillas().agregar(new Plantilla(nombre, asunto, cuerpo));
            salida.println("Plantilla creada: " + nombre.trim());
        } catch (IllegalArgumentException e) {
            salida.println("No se pudo crear: " + e.getMessage());
        }
    }

    private void verHistorial() {
        String usuario = leer("Filtrar por usuario (Enter = todos): ");
        String textoCanal = leer("Filtrar por canal CORREO/SMS/MENSAJERIA (Enter = todos): ");
        String textoEstado = leer("Filtrar por estado E=enviado, F=fallido (Enter = todos): ");
        if (usuario == null || textoCanal == null || textoEstado == null) {
            return;
        }

        TipoCanal canal = null;
        if (!textoCanal.isBlank()) {
            Optional<List<TipoCanal>> interpretado = interpretarCanales(textoCanal);
            if (interpretado.isEmpty() || interpretado.get().size() != 1) {
                salida.println("Canal no válido.");
                return;
            }
            canal = interpretado.get().get(0);
        }
        Boolean exitoso = null;
        if (textoEstado.trim().equalsIgnoreCase("E")) {
            exitoso = true;
        } else if (textoEstado.trim().equalsIgnoreCase("F")) {
            exitoso = false;
        } else if (!textoEstado.isBlank()) {
            salida.println("Estado no válido.");
            return;
        }

        List<RegistroEnvio> registros = sistema.getHistorial().consultar(usuario, canal, exitoso);
        if (registros.isEmpty()) {
            salida.println("No hay registros.");
            return;
        }
        for (RegistroEnvio r : registros) {
            salida.println(String.join(" | ",
                    r.fecha().format(FORMATO_FECHA),
                    r.usuario(),
                    r.canal() == null ? "SIN CANAL" : r.canal().name(),
                    r.estado(),
                    String.valueOf(r.intentos()),
                    r.detalle()));
        }
    }

    private void cambiarProbabilidadDeFallo() {
        String texto = leer("Nueva probabilidad de fallo entre 0 y 1 (por ejemplo 0.3): ");
        if (texto == null) {
            return;
        }
        try {
            sistema.setProbabilidadFallo(Double.parseDouble(texto.trim().replace(',', '.')));
            salida.println("Probabilidad de fallo actualizada a " + sistema.getProbabilidadFallo());
        } catch (IllegalArgumentException e) { // también cubre NumberFormatException
            salida.println("Valor no válido. Debe ser un número entre 0 y 1.");
        }
    }

    // ---------- utilidades de entrada y salida ----------

    /** Muestra una lista numerada y devuelve la opción elegida, o null si no hay o no es válida. */
    private <T> T elegir(String titulo, List<T> opciones, Function<T, String> nombre) {
        if (opciones.isEmpty()) {
            salida.println("No hay opciones disponibles para: " + titulo);
            return null;
        }
        salida.println(titulo + ":");
        for (int i = 0; i < opciones.size(); i++) {
            salida.println("  " + (i + 1) + ") " + nombre.apply(opciones.get(i)));
        }
        String texto = leer("Elige un número: ");
        if (texto == null) {
            return null;
        }
        try {
            int indice = Integer.parseInt(texto.trim());
            if (indice >= 1 && indice <= opciones.size()) {
                return opciones.get(indice - 1);
            }
        } catch (NumberFormatException e) {
            // se informa abajo
        }
        salida.println("Opción no válida.");
        return null;
    }

    /** Convierte "SMS, correo" en una lista de canales; vacío si algún nombre no existe. */
    private Optional<List<TipoCanal>> interpretarCanales(String texto) {
        List<TipoCanal> canales = new ArrayList<>();
        for (String parte : texto.split(",")) {
            if (parte.isBlank()) {
                continue;
            }
            try {
                canales.add(TipoCanal.valueOf(parte.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.of(canales);
    }

    /** Muestra el mensaje y lee una línea. Devuelve null si la entrada terminó. */
    private String leer(String pregunta) {
        salida.print(pregunta);
        salida.flush();
        return entrada.hasNextLine() ? entrada.nextLine() : null;
    }

    private static String textoONulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private static String valor(String texto) {
        return texto == null ? "-" : texto;
    }
}
