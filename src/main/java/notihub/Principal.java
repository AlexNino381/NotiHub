package notihub;

import java.util.List;
import notihub.canales.AdaptadorCorreo;
import notihub.canales.AdaptadorMensajeria;
import notihub.canales.AdaptadorSms;
import notihub.canales.CanalNotificacion;
import notihub.canales.ResultadoEnvio;
import notihub.canales.externos.ServicioCorreo;
import notihub.canales.externos.ServicioMensajeria;
import notihub.canales.externos.ServicioSms;
import notihub.modelo.Plantilla;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import notihub.nucleo.CentralNotificaciones;

/**
 * Demostración rápida del núcleo (días 1 y 2). El menú de consola real llega después.
 */
public class Principal {

    public static void main(String[] args) {
        List<CanalNotificacion> canales = List.of(
                new AdaptadorCorreo(new ServicioCorreo()),
                new AdaptadorSms(new ServicioSms()),
                new AdaptadorMensajeria(new ServicioMensajeria()));
        CentralNotificaciones central = new CentralNotificaciones(canales);

        Usuario ana = new Usuario("Ana", "ana@mail.com", "3001112233", "ana_chat",
                List.of(TipoCanal.SMS, TipoCanal.CORREO, TipoCanal.MENSAJERIA));
        Usuario luis = new Usuario("Luis", "luis@mail.com", null, null,
                List.of(TipoCanal.CORREO));

        Plantilla aviso = new Plantilla("aviso", "Aviso importante",
                "Hola {nombre}, esto es un aviso del {fecha}.");

        System.out.println("--- Mensaje URGENTE para Ana (todos sus canales) ---");
        imprimir(central.enviar(ana, aviso.generar(TipoMensaje.URGENTE, ana)));

        System.out.println("--- Mensaje INFORMATIVO para Ana (solo su canal principal) ---");
        imprimir(central.enviar(ana, aviso.generar(TipoMensaje.INFORMATIVO, ana)));

        System.out.println("--- Mensaje URGENTE para Luis (solo acepta correo) ---");
        imprimir(central.enviar(luis, aviso.generar(TipoMensaje.URGENTE, luis)));
    }

    private static void imprimir(List<ResultadoEnvio> resultados) {
        for (ResultadoEnvio r : resultados) {
            System.out.println((r.exitoso() ? "[OK]    " : "[FALLO] ")
                    + (r.canal() == null ? "-" : r.canal()) + ": " + r.detalle());
        }
    }
}
