package notihub.interfaz.grafica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import notihub.canales.ResultadoEnvio;
import notihub.modelo.Mensaje;
import notihub.modelo.Plantilla;
import notihub.modelo.TipoCanal;
import notihub.modelo.TipoMensaje;
import notihub.modelo.Usuario;
import notihub.nucleo.Sistema;
import notihub.registro.RegistroEnvio;

/**
 * Contenido de la ventana: cuatro pestañas (Enviar, Usuarios, Plantillas e Historial).
 * Igual que la consola, no tiene lógica de negocio: lee datos del núcleo
 * ({@link Sistema}), llama a sus operaciones y muestra el resultado.
 */
public class PanelNotiHub extends JPanel {

    private static final String TODOS = "(todos)";
    private static final String NINGUNO = "(ninguno)";

    private final Sistema sistema;

    // Pestaña Enviar
    private final JComboBox<String> comboUsuario = new JComboBox<>();
    private final JComboBox<String> comboPlantilla = new JComboBox<>();
    private final JComboBox<TipoMensaje> comboTipo = new JComboBox<>(TipoMensaje.values());
    private final JTextArea areaResultados = new JTextArea(10, 50);
    private final JSpinner spinnerFallo;

    // Pestaña Usuarios
    private final DefaultTableModel modeloUsuarios = tablaSoloLectura("Nombre", "Correo", "Teléfono", "Chat", "Canales");
    private final JTable tablaUsuarios = new JTable(modeloUsuarios);
    private final JTextField campoNombre = new JTextField(20);
    private final JTextField campoCorreo = new JTextField(20);
    private final JTextField campoTelefono = new JTextField(20);
    private final JTextField campoChat = new JTextField(20);
    private final List<JComboBox<String>> combosCanal = List.of(
            comboCanalOpcional(), comboCanalOpcional(), comboCanalOpcional());

    // Pestaña Plantillas
    private final DefaultTableModel modeloPlantillas = tablaSoloLectura("Nombre", "Asunto", "Cuerpo");
    private final JTextField campoNombrePlantilla = new JTextField(20);
    private final JTextField campoAsuntoPlantilla = new JTextField(20);
    private final JTextField campoCuerpoPlantilla = new JTextField(20);

    // Pestaña Historial
    private final ModeloTablaHistorial modeloHistorial = new ModeloTablaHistorial();
    private final JComboBox<String> filtroUsuario = new JComboBox<>();
    private final JComboBox<String> filtroCanal = new JComboBox<>(
            new String[]{TODOS, "CORREO", "SMS", "MENSAJERIA"});
    private final JComboBox<String> filtroEstado = new JComboBox<>(
            new String[]{TODOS, "Enviados", "Fallidos"});

    public PanelNotiHub(Sistema sistema) {
        super(new BorderLayout());
        this.sistema = sistema;
        this.spinnerFallo = new JSpinner(new SpinnerNumberModel(sistema.getProbabilidadFallo(), 0.0, 1.0, 0.1));
        spinnerFallo.setEditor(new JSpinner.NumberEditor(spinnerFallo, "0.0"));
        spinnerFallo.setPreferredSize(new Dimension(70, 26));

        areaResultados.setEditable(false);
        // Observer: cada envío del núcleo se escribe también en esta área de texto
        sistema.getCentral().agregarObservador(new RegistroAreaTexto(areaResultados));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Enviar", panelEnviar());
        pestanas.addTab("Usuarios", panelUsuarios());
        pestanas.addTab("Plantillas", panelPlantillas());
        pestanas.addTab("Historial", panelHistorial());
        add(pestanas, BorderLayout.CENTER);

        recargarUsuarios();
        recargarPlantillas();
        consultarHistorial();
    }

    // ---------- construcción de las pestañas ----------

    private JPanel panelEnviar() {
        JPanel formulario = formulario(
                new String[]{"Usuario", "Plantilla", "Tipo de mensaje"},
                new JComponent[]{comboUsuario, comboPlantilla, comboTipo});

        JButton botonEnviar = new JButton("Enviar mensaje");
        botonEnviar.addActionListener(e -> enviar());

        JButton botonAplicar = new JButton("Aplicar");
        botonAplicar.addActionListener(e -> aplicarProbabilidadDeFallo());
        JPanel panelFallo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFallo.add(new JLabel("Probabilidad de fallo de los canales (0 a 1):"));
        panelFallo.add(spinnerFallo);
        panelFallo.add(botonAplicar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(formulario, BorderLayout.NORTH);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(botonEnviar);
        superior.add(botones, BorderLayout.CENTER);
        superior.add(panelFallo, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelUsuarios() {
        JPanel formulario = formulario(
                new String[]{"Nombre", "Correo", "Teléfono", "Identificador de chat",
                        "Canal preferido 1", "Canal preferido 2", "Canal preferido 3"},
                new JComponent[]{campoNombre, campoCorreo, campoTelefono, campoChat,
                        combosCanal.get(0), combosCanal.get(1), combosCanal.get(2)});

        JButton botonRegistrar = new JButton("Registrar usuario");
        botonRegistrar.addActionListener(e -> registrarUsuario());
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        botonEliminar.addActionListener(e -> eliminarUsuario());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(botonRegistrar);
        botones.add(botonEliminar);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(formulario, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelPlantillas() {
        JPanel formulario = formulario(
                new String[]{"Nombre", "Asunto ({nombre}, {fecha})", "Cuerpo ({nombre}, {correo}, {telefono}, {fecha})"},
                new JComponent[]{campoNombrePlantilla, campoAsuntoPlantilla, campoCuerpoPlantilla});

        JButton botonCrear = new JButton("Crear plantilla");
        botonCrear.addActionListener(e -> crearPlantilla());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(botonCrear);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(formulario, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JScrollPane(new JTable(modeloPlantillas)), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelHistorial() {
        JButton botonConsultar = new JButton("Consultar");
        botonConsultar.addActionListener(e -> consultarHistorial());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Usuario:"));
        filtros.add(filtroUsuario);
        filtros.add(new JLabel("Canal:"));
        filtros.add(filtroCanal);
        filtros.add(new JLabel("Estado:"));
        filtros.add(filtroEstado);
        filtros.add(botonConsultar);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(modeloHistorial)), BorderLayout.CENTER);
        return panel;
    }

    // ---------- acciones (solo llaman al núcleo) ----------

    private void enviar() {
        Optional<Usuario> usuario = sistema.getAgenda().buscar((String) comboUsuario.getSelectedItem());
        Optional<Plantilla> plantilla = sistema.getPlantillas().buscar((String) comboPlantilla.getSelectedItem());
        if (usuario.isEmpty() || plantilla.isEmpty()) {
            mostrarError("Elige un usuario y una plantilla.");
            return;
        }
        try {
            Mensaje mensaje = plantilla.get().generar((TipoMensaje) comboTipo.getSelectedItem(), usuario.get());
            List<ResultadoEnvio> resultados = sistema.getCentral().enviar(usuario.get(), mensaje);
            long exitosos = resultados.stream().filter(ResultadoEnvio::exitoso).count();
            areaResultados.append("Resumen: " + exitosos + " exitoso(s), "
                    + (resultados.size() - exitosos) + " fallido(s).\n\n");
            consultarHistorial();
        } catch (IllegalArgumentException e) {
            mostrarError("No se pudo preparar el mensaje: " + e.getMessage());
        }
    }

    private void aplicarProbabilidadDeFallo() {
        sistema.setProbabilidadFallo(((Number) spinnerFallo.getValue()).doubleValue());
        areaResultados.append("Probabilidad de fallo actualizada a " + sistema.getProbabilidadFallo() + "\n\n");
    }

    private void registrarUsuario() {
        List<TipoCanal> canales = new ArrayList<>();
        for (JComboBox<String> combo : combosCanal) {
            String elegido = (String) combo.getSelectedItem();
            if (!NINGUNO.equals(elegido)) {
                canales.add(TipoCanal.valueOf(elegido));
            }
        }
        try {
            sistema.getAgenda().agregar(new Usuario(campoNombre.getText().trim(),
                    textoONulo(campoCorreo), textoONulo(campoTelefono), textoONulo(campoChat), canales));
            limpiar(campoNombre, campoCorreo, campoTelefono, campoChat);
            for (JComboBox<String> combo : combosCanal) {
                combo.setSelectedItem(NINGUNO);
            }
            recargarUsuarios();
        } catch (IllegalArgumentException e) {
            mostrarError("No se pudo registrar el usuario: " + e.getMessage());
        }
    }

    private void eliminarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0) {
            mostrarError("Selecciona un usuario de la tabla.");
            return;
        }
        sistema.getAgenda().eliminar((String) modeloUsuarios.getValueAt(fila, 0));
        recargarUsuarios();
    }

    private void crearPlantilla() {
        if (campoCuerpoPlantilla.getText().isBlank()) {
            mostrarError("El cuerpo de la plantilla no puede estar vacío.");
            return;
        }
        try {
            sistema.getPlantillas().agregar(new Plantilla(campoNombrePlantilla.getText(),
                    campoAsuntoPlantilla.getText(), campoCuerpoPlantilla.getText()));
            limpiar(campoNombrePlantilla, campoAsuntoPlantilla, campoCuerpoPlantilla);
            recargarPlantillas();
        } catch (IllegalArgumentException e) {
            mostrarError("No se pudo crear la plantilla: " + e.getMessage());
        }
    }

    private void consultarHistorial() {
        String usuario = (String) filtroUsuario.getSelectedItem();
        if (usuario == null || TODOS.equals(usuario)) {
            usuario = null;
        }
        String textoCanal = (String) filtroCanal.getSelectedItem();
        TipoCanal canal = TODOS.equals(textoCanal) ? null : TipoCanal.valueOf(textoCanal);
        String textoEstado = (String) filtroEstado.getSelectedItem();
        Boolean exitoso = TODOS.equals(textoEstado) ? null : "Enviados".equals(textoEstado);

        List<RegistroEnvio> registros = new ArrayList<>(sistema.getHistorial().consultar(usuario, canal, exitoso));
        Collections.reverse(registros); // lo más reciente primero
        modeloHistorial.setRegistros(registros);
    }

    // ---------- refrescos ----------

    private void recargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        comboUsuario.removeAllItems();
        Object filtroPrevio = filtroUsuario.getSelectedItem();
        filtroUsuario.removeAllItems();
        filtroUsuario.addItem(TODOS);
        for (Usuario u : sistema.getAgenda().listar()) {
            modeloUsuarios.addRow(new Object[]{u.getNombre(), u.getCorreo(), u.getTelefono(),
                    u.getIdMensajeria(), u.getCanalesPreferidos()});
            comboUsuario.addItem(u.getNombre());
            filtroUsuario.addItem(u.getNombre());
        }
        if (filtroPrevio != null) {
            filtroUsuario.setSelectedItem(filtroPrevio);
        }
    }

    private void recargarPlantillas() {
        modeloPlantillas.setRowCount(0);
        comboPlantilla.removeAllItems();
        for (Plantilla p : sistema.getPlantillas().listar()) {
            modeloPlantillas.addRow(new Object[]{p.getNombre(), p.getAsuntoModelo(), p.getCuerpoModelo()});
            comboPlantilla.addItem(p.getNombre());
        }
    }

    // ---------- utilidades de la interfaz ----------

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "NotiHub", JOptionPane.WARNING_MESSAGE);
    }

    private static JComboBox<String> comboCanalOpcional() {
        return new JComboBox<>(new String[]{NINGUNO, "CORREO", "SMS", "MENSAJERIA"});
    }

    private static DefaultTableModel tablaSoloLectura(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private static JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < etiquetas.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(etiquetas[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            panel.add(campos[i], c);
        }
        return panel;
    }

    private static String textoONulo(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    private static void limpiar(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }
}
