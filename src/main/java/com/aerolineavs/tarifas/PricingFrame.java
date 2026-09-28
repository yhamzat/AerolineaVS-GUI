package com.aerolineavs.tarifas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * Ventana de escritorio para solicitar una tarifa.
 */
public final class PricingFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO = new Color(244, 246, 242);
    private static final Color COLOR_TEXTO = new Color(32, 48, 44);
    private static final Color COLOR_SECUNDARIO = new Color(99, 115, 108);
    private static final Color COLOR_VERDE = new Color(30, 83, 72);
    private static final Color COLOR_RESULTADO = new Color(231, 239, 231);
    private static final Color COLOR_ACENTO = new Color(192, 112, 59);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 13);

    private final IPricingService pricingService;
    private final JSpinner edad = new JSpinner(new SpinnerNumberModel(30, 0, 120, 1));
    private final JSpinner vuelosAnuales = new JSpinner(new SpinnerNumberModel(6, 0, 365, 1));
    private final JSpinner ingresos = new JSpinner(new SpinnerNumberModel(25000.0, 0.0, 10000000.0, 500.0));
    private final JComboBox<TipoViajero> tipoViajero = crearCombo(TipoViajero.values());
    private final JComboBox<ClaseVuelo> claseVuelo = crearCombo(ClaseVuelo.values());
    private final JComboBox<RegionDestino> destino = crearCombo(RegionDestino.values());
    private final JCheckBox viajaConNinos = new JCheckBox("Viajo con menores de 12 años");
    private final JCheckBox viveConPadres = new JCheckBox("Vivo con mis padres");
    private final JLabel estadoResultado = new JLabel("RESULTADO");
    private final JLabel nombreTarifa = new JLabel("Completa tu perfil");
    private final JLabel descuento = new JLabel("--");
    private final JLabel suposiciones = new JLabel("El resultado aparecerá aquí al calcular la tarifa.");
    private JButton botonCalcular;

    public PricingFrame(IPricingService pricingService) {
        super("Tarifas de vuelo");
        this.pricingService = Objects.requireNonNull(pricingService, "pricingService no puede ser null");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 680));
        setSize(1040, 760);
        setLocationRelativeTo(null);

        configurarControles();
        construirVentana();
        tipoViajero.addActionListener(event -> actualizarDisponibilidadCampos());
        actualizarDisponibilidadCampos();
        getRootPane().setDefaultButton(botonCalcular);
    }

    private void configurarControles() {
        ingresos.setEditor(new JSpinner.NumberEditor(ingresos, "#,##0.00"));
        estiloControl(edad);
        estiloControl(vuelosAnuales);
        estiloControl(ingresos);
        estiloControl(tipoViajero);
        estiloControl(claseVuelo);
        estiloControl(destino);

        tipoViajero.setSelectedItem(TipoViajero.GENERAL);
        claseVuelo.setSelectedItem(ClaseVuelo.TURISTA);
        destino.setSelectedItem(RegionDestino.EUROPA);

        viajaConNinos.setFont(FUENTE_NORMAL);
        viveConPadres.setFont(FUENTE_NORMAL);
        viajaConNinos.setOpaque(false);
        viveConPadres.setOpaque(false);
    }

    private void construirVentana() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(COLOR_FONDO);
        root.add(crearCabecera(), BorderLayout.NORTH);

        JPanel contenido = new JPanel(new java.awt.GridLayout(1, 2, 18, 0));
        contenido.setBackground(COLOR_FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        contenido.add(crearFormulario());
        contenido.add(crearPanelResultado());
        root.add(contenido, BorderLayout.CENTER);
        root.add(crearPie(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_VERDE);
        header.setBorder(BorderFactory.createEmptyBorder(22, 30, 24, 30));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new javax.swing.BoxLayout(texto, javax.swing.BoxLayout.Y_AXIS));

        JLabel marca = new JLabel("AEROLÍNEA  /  TARIFAS");
        marca.setForeground(new Color(190, 215, 202));
        marca.setFont(FUENTE_ETIQUETA);

        JLabel titulo = new JLabel("Encuentra tu tarifa");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 29));

        JLabel subtitulo = new JLabel("Indica tus preferencias de viaje para consultar las opciones disponibles.");
        subtitulo.setForeground(new Color(224, 235, 228));
        subtitulo.setFont(FUENTE_NORMAL);

        texto.add(marca);
        texto.add(javax.swing.Box.createVerticalStrut(5));
        texto.add(titulo);
        texto.add(javax.swing.Box.createVerticalStrut(5));
        texto.add(subtitulo);
        header.add(texto, BorderLayout.CENTER);
        return header;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        agregarSeccion(formulario, 0, "PERFIL DE VIAJE");
        agregarCampo(formulario, 1, "Edad", edad);
        agregarCampo(formulario, 2, "Vuelos al año", vuelosAnuales);
        agregarCampo(formulario, 3, "Ingresos anuales (€)", ingresos);

        agregarSeccion(formulario, 4, "CRITERIOS DE TARIFA");
        agregarCampo(formulario, 5, "Tipo de viajero", tipoViajero);
        agregarCampo(formulario, 6, "Clase preferida", claseVuelo);
        agregarCampo(formulario, 7, "Destino habitual", destino);

        JPanel opciones = new JPanel();
        opciones.setOpaque(false);
        opciones.setLayout(new javax.swing.BoxLayout(opciones, javax.swing.BoxLayout.Y_AXIS));
        opciones.add(viajaConNinos);
        opciones.add(javax.swing.Box.createVerticalStrut(8));
        opciones.add(viveConPadres);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(18, 0, 0, 0);
        formulario.add(opciones, constraints);

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = 9;
        filler.weighty = 1;
        formulario.add(new JLabel(), filler);
        return formulario;
    }

    private JPanel crearPanelResultado() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_RESULTADO);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

        estadoResultado.setForeground(COLOR_VERDE);
        estadoResultado.setFont(FUENTE_ETIQUETA);
        nombreTarifa.setForeground(COLOR_TEXTO);
        nombreTarifa.setFont(new Font("Segoe UI", Font.BOLD, 25));
        nombreTarifa.setVerticalAlignment(SwingConstants.TOP);
        descuento.setForeground(COLOR_ACENTO);
        descuento.setFont(new Font("Segoe UI", Font.BOLD, 48));
        suposiciones.setForeground(COLOR_SECUNDARIO);
        suposiciones.setFont(FUENTE_NORMAL);
        suposiciones.setVerticalAlignment(SwingConstants.TOP);

        JLabel etiquetaDescuento = new JLabel("DESCUENTO");
        etiquetaDescuento.setForeground(COLOR_SECUNDARIO);
        etiquetaDescuento.setFont(FUENTE_ETIQUETA);

        panel.add(estadoResultado);
        panel.add(javax.swing.Box.createVerticalStrut(24));
        panel.add(nombreTarifa);
        panel.add(javax.swing.Box.createVerticalStrut(30));
        panel.add(etiquetaDescuento);
        panel.add(javax.swing.Box.createVerticalStrut(2));
        panel.add(descuento);
        panel.add(javax.swing.Box.createVerticalStrut(24));

        JPanel separador = new JPanel();
        separador.setBackground(new Color(203, 218, 205));
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setPreferredSize(new Dimension(1, 1));
        panel.add(separador);
        panel.add(javax.swing.Box.createVerticalStrut(18));
        panel.add(suposiciones);
        return panel;
    }

    private JPanel crearPie() {
        JPanel footer = new JPanel(new BorderLayout(16, 0));
        footer.setBackground(COLOR_FONDO);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 28, 24, 28));

        JLabel nota = new JLabel("La tarifa depende de las reglas disponibles para tu perfil.");
        nota.setForeground(COLOR_SECUNDARIO);
        nota.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        botonCalcular = crearBoton();
        footer.add(nota, BorderLayout.CENTER);
        footer.add(botonCalcular, BorderLayout.EAST);
        return footer;
    }

    private JButton crearBoton() {
        JButton boton = new JButton("Calcular tarifa");
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_VERDE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(13, 24, 13, 24));
        boton.addActionListener(event -> calcularTarifa());
        return boton;
    }

    private void calcularTarifa() {
        ClientePotencial cliente = new ClientePotencial(
                ((Number) edad.getValue()).intValue(),
                ((Number) vuelosAnuales.getValue()).intValue(),
                (TipoViajero) tipoViajero.getSelectedItem(),
                (ClaseVuelo) claseVuelo.getSelectedItem(),
                (RegionDestino) destino.getSelectedItem(),
                ((Number) ingresos.getValue()).doubleValue(),
                viajaConNinos.isSelected(),
                viveConPadres.isSelected()
        );

        ResultadoTarifa resultado = pricingService.evaluar(cliente);
        Tarifa tarifa = resultado.tarifa();
        boolean sinTarifa = tarifa == Tarifa.SIN_TARIFA;

        estadoResultado.setText(sinTarifa ? "SIN COINCIDENCIA" : "TARIFA RECOMENDADA");
        nombreTarifa.setText("<html><div style='width: 280px'>" + tarifa.getNombre() + "</div></html>");
        descuento.setText(tarifa.getDescuentoPorcentaje() + "%");
        suposiciones.setText("<html><div style='width: 280px'>" + resultado.suposiciones() + "</div></html>");
    }

    private void actualizarDisponibilidadCampos() {
        viveConPadres.setEnabled(tipoViajero.getSelectedItem() == TipoViajero.TRABAJADOR_JOVEN);
    }

    private static void agregarSeccion(JPanel panel, int fila, String titulo) {
        JLabel label = new JLabel(titulo);
        label.setForeground(COLOR_VERDE);
        label.setFont(FUENTE_ETIQUETA);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = fila;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(fila == 0 ? 0 : 20, 0, 10, 0);
        panel.add(label, constraints);
    }

    private static void agregarCampo(JPanel panel, int fila, String texto, JComponent control) {
        JLabel label = new JLabel(texto);
        label.setForeground(COLOR_TEXTO);
        label.setFont(FUENTE_NORMAL);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = fila;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 0, 5, 14);
        panel.add(label, labelConstraints);

        GridBagConstraints controlConstraints = new GridBagConstraints();
        controlConstraints.gridx = 1;
        controlConstraints.gridy = fila;
        controlConstraints.weightx = 1;
        controlConstraints.fill = GridBagConstraints.HORIZONTAL;
        controlConstraints.insets = new Insets(5, 0, 5, 0);
        control.setPreferredSize(new Dimension(190, 36));
        panel.add(control, controlConstraints);
    }

    private static void estiloControl(JComponent control) {
        control.setFont(FUENTE_NORMAL);
        control.setForeground(COLOR_TEXTO);
        control.setBackground(Color.WHITE);
        control.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 216, 210)),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)
        ));
    }

    private static <E extends Enum<E>> JComboBox<E> crearCombo(E[] opciones) {
        JComboBox<E> combo = new JComboBox<>(opciones);
        combo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int indice, boolean seleccionado, boolean tieneFoco) {
                String etiqueta = valor instanceof Enum<?> opcion ? nombreVisible(opcion) : "";
                return super.getListCellRendererComponent(lista, etiqueta, indice, seleccionado, tieneFoco);
            }
        });
        return combo;
    }

    private static String nombreVisible(Enum<?> opcion) {
        if (opcion == RegionDestino.AMERICA) {
            return "América";
        }
        String texto = opcion.name().replace('_', ' ').toLowerCase(Locale.forLanguageTag("es"));
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}