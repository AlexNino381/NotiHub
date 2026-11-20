package notihub.nucleo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import notihub.modelo.Plantilla;

/**
 * Plantillas de mensaje disponibles. Trae algunas de ejemplo y permite crear más
 * mientras la aplicación está en ejecución.
 */
public class CatalogoPlantillas {

    private final Map<String, Plantilla> plantillas = new LinkedHashMap<>();

    public CatalogoPlantillas() {
        agregar(new Plantilla("bienvenida",
                "Bienvenido a NotiHub, {nombre}",
                "Hola {nombre}, tu cuenta quedó registrada el {fecha}."));
        agregar(new Plantilla("recordatorio",
                "Recordatorio para {nombre}",
                "Hola {nombre}, te recordamos que tienes un trámite pendiente. Aviso del {fecha}."));
        agregar(new Plantilla("alerta",
                "Alerta de seguridad",
                "{nombre}, detectamos un acceso inusual a tu cuenta el {fecha}. Revisa tu cuenta."));
    }

    public List<Plantilla> listar() {
        return new ArrayList<>(plantillas.values());
    }

    /** Busca por nombre, sin distinguir mayúsculas. */
    public Optional<Plantilla> buscar(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(plantillas.get(nombre.trim().toLowerCase()));
    }

    public void agregar(Plantilla plantilla) {
        String clave = plantilla.getNombre().trim().toLowerCase();
        if (plantillas.containsKey(clave)) {
            throw new IllegalArgumentException("Ya existe una plantilla llamada " + plantilla.getNombre());
        }
        plantillas.put(clave, plantilla);
    }
}
