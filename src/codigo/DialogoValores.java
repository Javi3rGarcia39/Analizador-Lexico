package codigo;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;

/**
 * Dialogo que le pide al usuario el valor de cada variable
 * declarada sin valor inicial en el codigo.
 */
public class DialogoValores extends JDialog {

    private Map<String, JTextField> campos = new LinkedHashMap<>();
    private Map<String, Integer> resultado = null;
    private boolean cancelado = false;

    public DialogoValores(Frame padre, Map<String, String> variablesSinValor) {
        super(padre, "Ingresar valores de variables", true);
        setLayout(new BorderLayout(10, 10));

        // Panel de campos
        JPanel panelCampos = new JPanel(new GridLayout(variablesSinValor.size(), 2, 8, 8));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        for (Map.Entry<String, String> entry : variablesSinValor.entrySet()) {
            String nombre = entry.getKey();
            String tipo   = entry.getValue();

            JLabel label = new JLabel(tipo + " " + nombre + ":");
            label.setFont(new Font("Tahoma", Font.PLAIN, 15));

            JTextField campo = new JTextField("0", 10);
            campo.setFont(new Font("Tahoma", Font.PLAIN, 15));

            campos.put(nombre, campo);
            panelCampos.add(label);
            panelCampos.add(campo);
        }

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEjecutar = new JButton("Ejecutar");
        JButton btnCancelar = new JButton("Cancelar");

        btnEjecutar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnCancelar.setFont(new Font("Tahoma", Font.PLAIN, 15));

        btnEjecutar.addActionListener(e -> {
            if (validarCampos()) {
                recogerValores();
                dispose();
            }
        });

        btnCancelar.addActionListener(e -> {
            cancelado = true;
            dispose();
        });

        panelBotones.add(btnEjecutar);
        panelBotones.add(btnCancelar);

        add(panelCampos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(350, 100));
        setLocationRelativeTo(padre);
        setResizable(false);
    }

    private boolean validarCampos() {
        for (Map.Entry<String, JTextField> entry : campos.entrySet()) {
            String texto = entry.getValue().getText().trim();
            try {
                Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "El valor de '" + entry.getKey() + "' debe ser un numero entero.",
                    "Error de entrada", JOptionPane.ERROR_MESSAGE);
                entry.getValue().requestFocus();
                return false;
            }
        }
        return true;
    }

    private void recogerValores() {
        resultado = new LinkedHashMap<>();
        for (Map.Entry<String, JTextField> entry : campos.entrySet()) {
            resultado.put(entry.getKey(), Integer.parseInt(entry.getValue().getText().trim()));
        }
    }

    /** Devuelve los valores ingresados, o null si el usuario cancelo. */
    public Map<String, Integer> getResultado() {
        return cancelado ? null : resultado;
    }
}
