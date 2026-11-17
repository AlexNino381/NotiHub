package notihub.modelo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

/**
 * Persona que recibe notificaciones. Guarda sus datos de contacto y los canales
 * que acepta, en orden de preferencia (el primero es el de mayor prioridad).
 */
public class Usuario {

    private final String nombre;
    private final String correo;
    private final String telefono;
    private final String idMensajeria;
    private final List<TipoCanal> canalesPreferidos;

    public Usuario(String nombre, String correo, String telefono,
                   String idMensajeria, List<TipoCanal> canalesPreferidos) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio");
        }
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.idMensajeria = idMensajeria;

        // LinkedHashSet: conserva el orden y elimina canales repetidos
        Set<TipoCanal> sinRepetidos = new LinkedHashSet<>();
        if (canalesPreferidos != null) {
            sinRepetidos.addAll(canalesPreferidos);
        }
        this.canalesPreferidos = new ArrayList<>(sinRepetidos);
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getIdMensajeria() {
        return idMensajeria;
    }

    /** Canales aceptados por el usuario, de mayor a menor preferencia. */
    public List<TipoCanal> getCanalesPreferidos() {
        return Collections.unmodifiableList(canalesPreferidos);
    }

    public boolean acepta(TipoCanal canal) {
        return canalesPreferidos.contains(canal);
    }

    /** Canal de mayor preferencia, o vacío si el usuario no acepta ninguno. */
    public Optional<TipoCanal> getCanalPrincipal() {
        return canalesPreferidos.isEmpty()
                ? Optional.empty()
                : Optional.of(canalesPreferidos.get(0));
    }
}
