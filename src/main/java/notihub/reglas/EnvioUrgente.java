package notihub.reglas;

import java.util.List;
import notihub.modelo.TipoCanal;

/**
 * PATRÓN STRATEGY - rol "ConcreteStrategy".
 * Un mensaje urgente sale por todos los canales que el usuario acepta.
 */
public class EnvioUrgente implements EstrategiaEnvio {

    @Override
    public List<TipoCanal> elegirCanales(List<TipoCanal> canalesAceptados) {
        return List.copyOf(canalesAceptados);
    }
}
