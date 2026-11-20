package notihub.nucleo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import notihub.canales.AdaptadorCorreo;
import notihub.canales.AdaptadorMensajeria;
import notihub.canales.AdaptadorSms;
import notihub.canales.externos.ServicioCorreo;
import notihub.canales.externos.ServicioMensajeria;
import notihub.canales.externos.ServicioSimulado;
import notihub.canales.externos.ServicioSms;
import notihub.modelo.TipoCanal;
import notihub.modelo.Usuario;
import notihub.persistencia.RepositorioUsuarios;
import notihub.registro.HistorialEnvios;
import notihub.registro.RegistroArchivo;

/**
 * Raíz de composición: arma todas las piezas del núcleo y las deja listas para que
 * las interfaces (consola o gráfica) las usen. Es el único lugar que conoce las
 * clases concretas de canales, servicios y observadores.
 *
 * Los datos se guardan en la carpeta indicada: usuarios.csv e historial.csv.
 */
public class Sistema {

    private final CentralNotificaciones central;
    private final AgendaUsuarios agenda;
    private final CatalogoPlantillas plantillas;
    private final HistorialEnvios historial;
    private final List<ServicioSimulado> servicios;

    private Sistema(CentralNotificaciones central, AgendaUsuarios agenda, CatalogoPlantillas plantillas,
                    HistorialEnvios historial, List<ServicioSimulado> servicios) {
        this.central = central;
        this.agenda = agenda;
        this.plantillas = plantillas;
        this.historial = historial;
        this.servicios = servicios;
    }

    /**
     * @param carpetaDatos      carpeta donde se guardan los archivos CSV
     * @param probabilidadFallo probabilidad inicial de fallo de los canales simulados (0.0 a 1.0)
     */
    public static Sistema iniciar(Path carpetaDatos, double probabilidadFallo) {
        Path archivoUsuarios = carpetaDatos.resolve("usuarios.csv");
        boolean primeraVez = Files.notExists(archivoUsuarios);

        ServicioCorreo correo = new ServicioCorreo(probabilidadFallo);
        ServicioSms sms = new ServicioSms(probabilidadFallo);
        ServicioMensajeria mensajeria = new ServicioMensajeria(probabilidadFallo);
        CentralNotificaciones central = new CentralNotificaciones(List.of(
                new AdaptadorCorreo(correo), new AdaptadorSms(sms), new AdaptadorMensajeria(mensajeria)));

        RegistroArchivo registroArchivo = new RegistroArchivo(carpetaDatos.resolve("historial.csv"));
        HistorialEnvios historial = new HistorialEnvios();
        historial.cargar(registroArchivo.cargar()); // recupera lo enviado en ejecuciones anteriores
        central.agregarObservador(historial);
        central.agregarObservador(registroArchivo);

        AgendaUsuarios agenda = new AgendaUsuarios(new RepositorioUsuarios(archivoUsuarios));
        if (primeraVez) {
            agenda.agregar(new Usuario("Ana", "ana@mail.com", "3001112233", "ana_chat",
                    List.of(TipoCanal.SMS, TipoCanal.CORREO, TipoCanal.MENSAJERIA)));
            agenda.agregar(new Usuario("Luis", "luis@mail.com", null, null,
                    List.of(TipoCanal.CORREO)));
        }

        return new Sistema(central, agenda, new CatalogoPlantillas(), historial,
                List.of(correo, sms, mensajeria));
    }

    public CentralNotificaciones getCentral() {
        return central;
    }

    public AgendaUsuarios getAgenda() {
        return agenda;
    }

    public CatalogoPlantillas getPlantillas() {
        return plantillas;
    }

    public HistorialEnvios getHistorial() {
        return historial;
    }

    public double getProbabilidadFallo() {
        return servicios.get(0).getProbabilidadFallo();
    }

    /** Cambia la probabilidad de fallo de los tres canales simulados. */
    public void setProbabilidadFallo(double probabilidad) {
        for (ServicioSimulado servicio : servicios) {
            servicio.setProbabilidadFallo(probabilidad);
        }
    }
}
