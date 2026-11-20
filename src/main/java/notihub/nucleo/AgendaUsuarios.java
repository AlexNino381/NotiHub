package notihub.nucleo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import notihub.modelo.Usuario;
import notihub.persistencia.RepositorioUsuarios;

/**
 * Lista de usuarios del sistema. Cada cambio se guarda de inmediato en el archivo,
 * y al crearla se cargan los usuarios guardados anteriormente.
 */
public class AgendaUsuarios {

    private final RepositorioUsuarios repositorio;
    private final List<Usuario> usuarios;

    public AgendaUsuarios(RepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
        this.usuarios = new ArrayList<>(repositorio.cargar());
    }

    public List<Usuario> listar() {
        return List.copyOf(usuarios);
    }

    /** Busca por nombre, sin distinguir mayúsculas. */
    public Optional<Usuario> buscar(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return usuarios.stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }

    public void agregar(Usuario usuario) {
        if (buscar(usuario.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario llamado " + usuario.getNombre());
        }
        usuarios.add(usuario);
        repositorio.guardar(usuarios);
    }

    /** @return true si existía y fue eliminado */
    public boolean eliminar(String nombre) {
        Optional<Usuario> encontrado = buscar(nombre);
        if (encontrado.isEmpty()) {
            return false;
        }
        usuarios.remove(encontrado.get());
        repositorio.guardar(usuarios);
        return true;
    }
}
