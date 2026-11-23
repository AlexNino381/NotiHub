package notihub.interfaz.grafica;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import notihub.registro.RegistroEnvio;

/**
 * Adapta una lista de {@link RegistroEnvio} para mostrarla en una JTable.
 * Solo presenta datos: no contiene lógica de negocio.
 */
public class ModeloTablaHistorial extends AbstractTableModel {

    private static final String[] COLUMNAS = {"Fecha", "Usuario", "Canal", "Estado", "Intentos", "Detalle"};
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<RegistroEnvio> registros = new ArrayList<>();

    public void setRegistros(List<RegistroEnvio> nuevos) {
        this.registros = new ArrayList<>(nuevos);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return registros.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        RegistroEnvio r = registros.get(fila);
        return switch (columna) {
            case 0 -> r.fecha().format(FORMATO_FECHA);
            case 1 -> r.usuario();
            case 2 -> r.canal() == null ? "SIN CANAL" : r.canal().name();
            case 3 -> r.estado();
            case 4 -> r.intentos();
            default -> r.detalle();
        };
    }
}
