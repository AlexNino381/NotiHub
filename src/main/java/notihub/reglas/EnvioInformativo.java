package notihub.reglas;

import java.util.List;
import notihub.modelo.TipoCanal;

/**
 * PATRÓN STRATEGY - rol "ConcreteStrategy".
 * Un mensaje informativo sale solo por el canal preferido del usuario.
 */
public class EnvioInformativo implements EstrategiaEnvio {

    @Override
    public List<TipoCanal> elegirCanales(List<TipoCanal> canalesAceptados) {
        if (canalesAceptados.isEmpty()) {
            return List.of();
        }
        return List.of(canalesAceptados.get(0));
    }
}
