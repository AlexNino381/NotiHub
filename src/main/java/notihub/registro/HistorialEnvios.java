package notihub.registro;

import java.util.ArrayList;
import java.util.List;
import notihub.modelo.TipoCanal;

/**
 * PATRÓN OBSERVER - rol "ConcreteObserver".
 * Guarda en memoria todos los envíos y permite consultarlos con filtros.
 */
public class HistorialEnvios implements ObservadorEnvio {

    private final List<RegistroEnvio> registros = new ArrayList<>();

    @Override
    public void alRegistrarEnvio(RegistroEnvio registro) {
        registros.add(registro);
    }

    /** Carga registros anteriores (por ejemplo, los leídos del archivo al iniciar). */
    public void cargar(List<RegistroEnvio> anteriores) {
        registros.addAll(anteriores);
    }

    public List<RegistroEnvio> todos() {
        return List.copyOf(registros);
    }

    /**
     * Consulta con filtros opcionales: un filtro nulo (o un usuario en blanco)
     * significa "no filtrar por ese dato".
     *
     * @param usuario nombre del usuario, sin distinguir mayúsculas
     * @param canal   canal usado
     * @param exitoso true solo enviados, false solo fallidos
     */
    public List<RegistroEnvio> consultar(String usuario, TipoCanal canal, Boolean exitoso) {
        List<RegistroEnvio> resultado = new ArrayList<>();
        for (RegistroEnvio r : registros) {
            boolean coincideUsuario = usuario == null || usuario.isBlank()
                    || r.usuario().equalsIgnoreCase(usuario.trim());
            boolean coincideCanal = canal == null || r.canal() == canal;
            boolean coincideEstado = exitoso == null || r.exitoso() == exitoso;
            if (coincideUsuario && coincideCanal && coincideEstado) {
                resultado.add(r);
            }
        }
        return resultado;
    }
}
