package notihub.registro;

/**
 * PATRÓN OBSERVER - rol "Observer".
 *
 * Quien implemente esta interfaz se entera de cada envío realizado. Así el núcleo
 * puede avisar a cualquier cantidad de destinos (consola, archivo, memoria, una
 * tabla de la interfaz gráfica) sin conocer ninguno de ellos.
 */
public interface ObservadorEnvio {

    void alRegistrarEnvio(RegistroEnvio registro);
}
