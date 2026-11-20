package notihub.reglas;

import java.util.List;
import notihub.modelo.TipoCanal;

/**
 * PATRÓN STRATEGY - rol "Strategy".
 *
 * Define cómo se decide por cuáles canales se envía un mensaje. Cada regla de
 * negocio (urgente, informativo, y las que aparezcan) es una implementación
 * distinta, intercambiable sin tocar a quien la usa.
 */
public interface EstrategiaEnvio {

    /**
     * @param canalesAceptados canales disponibles que el usuario acepta, ya en su
     *                         orden de preferencia (el primero es el preferido)
     * @return los canales por los que realmente se enviará el mensaje
     */
    List<TipoCanal> elegirCanales(List<TipoCanal> canalesAceptados);
}
