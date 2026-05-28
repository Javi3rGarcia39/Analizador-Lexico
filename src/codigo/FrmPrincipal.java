package codigo;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.border.TitledBorder;

public class FrmPrincipal extends javax.swing.JFrame {

    // -----------------------------------------------------------------------
    // Colores modo oscuro
    // -----------------------------------------------------------------------
    private static final Color D_BG = new Color(28, 28, 36);
    private static final Color D_PANEL = new Color(36, 36, 48);
    private static final Color D_INPUT = new Color(24, 36, 50);
    private static final Color D_LEX_OUT = new Color(20, 40, 55);
    private static final Color D_SIN_OUT = new Color(32, 32, 44);
    private static final Color D_SAL_OUT = new Color(36, 36, 28);
    private static final Color D_BTN = new Color(50, 50, 70);
    private static final Color D_FG = new Color(210, 215, 225);
    private static final Color D_TITLE = new Color(100, 180, 255);
    private static final Color D_BORDER = new Color(60, 60, 90);

    // Colores modo claro (originales)
    private static final Color L_BG = new Color(240, 240, 240);
    private static final Color L_PANEL = Color.WHITE;
    private static final Color L_INPUT = new Color(204, 255, 255);
    private static final Color L_LEX_OUT = new Color(153, 255, 255);
    private static final Color L_SIN_OUT = new Color(204, 204, 204);
    private static final Color L_SAL_OUT = new Color(255, 255, 230);
    private static final Color L_BTN = new Color(220, 220, 220);
    private static final Color L_FG = Color.BLACK;
    private static final Color L_TITLE = Color.BLACK;
    private static final Color L_BORDER = Color.GRAY;

    private boolean modoOscuro = false;

    public FrmPrincipal() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Analizador Lexico / Sintactico");
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    // -----------------------------------------------------------------------
    // Tema
    // -----------------------------------------------------------------------
    private void aplicarTema() {
        Color bg = modoOscuro ? D_BG : L_BG;
        Color panel = modoOscuro ? D_PANEL : L_PANEL;
        Color input = modoOscuro ? D_INPUT : L_INPUT;
        Color lexOut = modoOscuro ? D_LEX_OUT : L_LEX_OUT;
        Color sinOut = modoOscuro ? D_SIN_OUT : L_SIN_OUT;
        Color salOut = modoOscuro ? D_SAL_OUT : L_SAL_OUT;
        Color btn = modoOscuro ? D_BTN : L_BTN;
        Color fg = modoOscuro ? D_FG : L_FG;
        Color title = modoOscuro ? D_TITLE : L_TITLE;
        Color border = modoOscuro ? D_BORDER : L_BORDER;

        getContentPane().setBackground(bg);
        panelCentral.setBackground(bg);

        // Panel 1
        jPanel1.setBackground(panel);
        setBorder(jPanel1, "Analizador Lexico", title, border);
        txtResultado.setBackground(input);
        txtResultado.setForeground(fg);
        txtResultado.setCaretColor(fg);
        txtAnalizarLex.setBackground(lexOut);
        txtAnalizarLex.setForeground(fg);
        jScrollPane1.getViewport().setBackground(input);
        jScrollPane2.getViewport().setBackground(lexOut);

        // Panel 2
        jPanel2.setBackground(panel);
        setBorder(jPanel2, "Analizador Sintactico", title, border);
        txtAnalizarSin.setBackground(sinOut);
        jScrollPane3.getViewport().setBackground(sinOut);

        // Panel 3
        jPanel3.setBackground(panel);
        setBorder(jPanel3, "Resultado de Ejecucion", title, border);
        txtSalida.setBackground(salOut);
        txtSalida.setForeground(fg);
        jScrollPane4.getViewport().setBackground(salOut);

        // Botones
        estilizarBtn(btnArchivo, btn, fg);
        estilizarBtn(btnAnalizarLex, btn, fg);
        estilizarBtn(btnLimpiarLex, btn, fg);
        estilizarBtn(btnAnalizarSin, btn, fg);
        estilizarBtn(btnLimpiarSin, btn, fg);
        estilizarBtn(btnLimpiarSalida, btn, fg);
        estilizarBtn(btnTema, btn, fg);
        btnTema.setText(modoOscuro ? "Modo Claro" : "Modo Oscuro");

        repaint();
        revalidate();
    }

    private void setBorder(javax.swing.JPanel p, String titulo, Color fg, Color linea) {
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(linea, 1),
                titulo, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 18), fg));
    }

    private void estilizarBtn(javax.swing.JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
    }

    // -----------------------------------------------------------------------
    // Analisis lexico
    // -----------------------------------------------------------------------
    private void analizarLexico() throws IOException {
        String expr = txtResultado.getText();
        LexerCup lexer = new LexerCup(new StringReader(expr));
        // Encabezado con formato fijo — columna LINEA de 10 chars, SIMBOLO alineado
        StringBuilder resultado = new StringBuilder(String.format("%-25s %s%n", "LINEA", "SIMBOLO"));

        while (true) {
            Symbol token = lexer.next_token();
            if (token.sym == 0) {
                txtAnalizarLex.setText(resultado.toString());
                return;
            }
            String lex = (token.value != null) ? token.value.toString() : "";
            String tipo;
            switch (token.sym) {
                case sym.Main:
                    tipo = "<Main>";
                    break;
                case sym.Llave_a:
                    tipo = "<Llave apertura>";
                    break;
                case sym.Llave_c:
                    tipo = "<Llave cierre>";
                    break;
                case sym.T_dato:
                    tipo = "<Tipo dato>";
                    break;
                case sym.Identificador:
                    tipo = "<Identificador>";
                    break;
                case sym.Numero:
                    tipo = "<Numero>";
                    break;
                case sym.Igual:
                    tipo = "<Asignacion>";
                    break;
                case sym.P_coma:
                    tipo = "<Fin sentencia>";
                    break;
                case sym.If:
                    tipo = "<Mision>";
                    break;
                case sym.Else:
                    tipo = "<Sino>";
                    break;
                case sym.While:
                    tipo = "<Mientras>";
                    break;
                case sym.Do:
                    tipo = "<Repetir>";
                    break;
                case sym.For:
                    tipo = "<Para>";
                    break;
                case sym.Op_relacional:
                    tipo = "<Op relacional>";
                    break;
                case sym.Op_booleano:
                    tipo = "<Op booleano>";
                    break;
                case sym.Op_incremento:
                    tipo = "<Incremento>";
                    break;
                case sym.Op_atribucion:
                    tipo = "<Atribucion (" + lex + ")>";
                    break;
                case sym.Parentesis_a:
                    tipo = "<Inicio condicion>";
                    break;
                case sym.Parentesis_c:
                    tipo = "<Fin condicion>";
                    break;
                case sym.Imprimir:
                    tipo = "<Mostrar>";
                    break;
                case sym.Accion:
                    tipo = "<Accion>";
                    break;
                case sym.Comillas:
                    tipo = "<Comillas>";
                    break;
                case sym.ERROR:
                    tipo = "<ERROR LEXICO>";
                    break;
                case sym.Suma:
                    tipo = "<Suma>";
                    break;
                case sym.Resta:
                    tipo = "<Resta>";
                    break;
                case sym.Multiplicacion:
                    tipo = "<Multiplicacion>";
                    break;
                case sym.Division:
                    tipo = "<Division>";
                    break;
                default:
                    tipo = "<" + lex + ">";
                    lex = "";
                    break;
            }
            resultado.append(String.format("  %-23s %s%n", tipo, lex));
        }
    }

    private String verificarLexico(String fuente) throws Exception {
        LexerCup lexer = new LexerCup(new StringReader(fuente));
        while (true) {
            Symbol token = lexer.next_token();
            if (token.sym == sym.EOF) {
                break;
            }
            if (token.sym == sym.ERROR) {
                int linea = token.left + 1;
                int columna = token.right + 1;
                String lex = token.value != null ? token.value.toString() : "?";
                return "Error LEXICO en linea " + linea
                        + ", columna " + columna
                        + ". Simbolo no reconocido: \"" + lex + "\"";
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Analisis sintactico + interpretacion
    // -----------------------------------------------------------------------
    private void analizarSintactico() {
        String ST = txtResultado.getText();

        try {
            String errorLexico = verificarLexico(ST);
            if (errorLexico != null) {
                txtAnalizarSin.setForeground(new Color(200, 80, 0));
                txtAnalizarSin.setText(errorLexico);
                txtSalida.setForeground(new Color(200, 80, 0));
                txtSalida.setText("No se puede ejecutar: hay errores lexicos.\n\n" + errorLexico);
                return;
            }
        } catch (Exception ex) {
            txtAnalizarSin.setForeground(Color.RED);
            txtAnalizarSin.setText("Error al analizar: " + ex.getMessage());
            return;
        }

// Verificar lexico primero
        try {
            LexerCup lexerCheck = new LexerCup(new StringReader(ST));
            while (true) {
                Symbol tokCheck = lexerCheck.next_token();
                if (tokCheck.sym == sym.EOF) {
                    break;
                }
                if (tokCheck.sym == sym.ERROR) {
                    int linea = tokCheck.left + 1;
                    int columna = tokCheck.right + 1;
                    String lex = tokCheck.value != null ? tokCheck.value.toString() : "?";
                    String msg = "Error LEXICO en linea " + linea
                            + ", columna " + columna
                            + ". Simbolo no reconocido: \"" + lex + "\"";
                    txtAnalizarSin.setForeground(new Color(200, 80, 0));
                    txtAnalizarSin.setText(msg);
                    txtSalida.setForeground(new Color(200, 80, 0));
                    txtSalida.setText("No se puede ejecutar: hay errores lexicos.\n\n" + msg);
                    return;
                }
            }
        } catch (Exception ex) {
            txtAnalizarSin.setForeground(Color.RED);
            txtAnalizarSin.setText("Error al analizar: " + ex.getMessage());
            return;
        }

        Sintax s = new Sintax(new LexerCup(new StringReader(ST)));
        try {
            s.parse();
        } catch (Exception ex) {
            Symbol err = s.getS();
            String msg = (err != null)
                    ? "Error SINTACTICO en linea " + (err.left + 1)
                    + ", columna " + (err.right + 1)
                    + ". Token inesperado: \"" + err.value + "\""
                    : "Error SINTACTICO desconocido.";
            txtAnalizarSin.setForeground(Color.RED);
            txtAnalizarSin.setText(msg);
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("No se puede ejecutar: hay errores sintacticos.\n\n" + msg);
            return;
        }

        txtAnalizarSin.setForeground(new Color(25, 111, 61));
        txtAnalizarSin.setText("Analisis sintactico correcto.");

        Interprete interprete = new Interprete();
        Map<String, String> sinValor;
        try {
            sinValor = interprete.detectarVariablesSinValor(ST);
        } catch (Exception ex) {
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("Error al detectar variables: " + ex.getMessage());
            return;
        }

        Map<String, Integer> valoresUsuario = null;
        if (!sinValor.isEmpty()) {
            DialogoValores dialogo = new DialogoValores(this, sinValor);
            dialogo.setVisible(true);
            valoresUsuario = dialogo.getResultado();
            if (valoresUsuario == null) {
                txtSalida.setForeground(new Color(150, 100, 0));
                txtSalida.setText("Ejecucion cancelada por el usuario.");
                return;
            }
        }

        try {
            String resultado = interprete.ejecutar(ST, valoresUsuario);
            txtSalida.setForeground(modoOscuro ? D_FG : new Color(30, 30, 30));
            txtSalida.setText("-- Resultado de ejecucion --\n\n" + resultado);
        } catch (Exception ex) {
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("Error en ejecucion: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnArchivo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAnalizarLex = new javax.swing.JTextArea();
        btnAnalizarLex = new javax.swing.JButton();
        btnLimpiarLex = new javax.swing.JButton();
        btnTema = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAnalizarSin = new javax.swing.JTextArea();
        btnAnalizarSin = new javax.swing.JButton();
        btnLimpiarSin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtSalida = new javax.swing.JTextArea();
        btnLimpiarSalida = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // --- jPanel1 ---
        jPanel1.setBackground(L_PANEL);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analizador Lexico",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 18)));

        btnArchivo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnArchivo.setText("Abrir archivo");
        btnArchivo.addActionListener(evt -> btnArchivoActionPerformed(evt));

        btnAnalizarLex.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnAnalizarLex.setText("Analizar");
        btnAnalizarLex.addActionListener(evt -> btnAnalizarLexActionPerformed(evt));

        btnLimpiarLex.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnLimpiarLex.setText("Limpiar");
        btnLimpiarLex.addActionListener(evt -> btnLimpiarLexActionPerformed(evt));

        btnTema.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnTema.setText("Modo Oscuro");
        btnTema.addActionListener(evt -> {
            modoOscuro = !modoOscuro;
            aplicarTema();
        });

        txtResultado.setBackground(L_INPUT);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResultado.setColumns(20);
        txtResultado.setRows(5);
        jScrollPane1.setViewportView(txtResultado);

        txtAnalizarLex.setEditable(false);
        txtAnalizarLex.setBackground(L_LEX_OUT);
        txtAnalizarLex.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtAnalizarLex.setColumns(20);
        txtAnalizarLex.setRows(5);
        jScrollPane2.setViewportView(txtAnalizarLex);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addComponent(btnAnalizarLex, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addComponent(btnLimpiarLex, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addComponent(btnTema, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jScrollPane2))
                                .addGap(18))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(8)
                                                .addComponent(btnTema, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10)
                                                .addComponent(btnAnalizarLex, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(8)
                                                .addComponent(btnLimpiarLex, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12))
        );

        // --- jPanel2 ---
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analizador Sintactico",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 18)));

        txtAnalizarSin.setEditable(false);
        txtAnalizarSin.setBackground(L_SIN_OUT);
        txtAnalizarSin.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtAnalizarSin.setColumns(20);
        txtAnalizarSin.setRows(3);
        jScrollPane3.setViewportView(txtAnalizarSin);

        btnAnalizarSin.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnAnalizarSin.setText("Analizar");
        btnAnalizarSin.addActionListener(evt -> btnAnalizarSinActionPerformed(evt));

        btnLimpiarSin.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnLimpiarSin.setText("Limpiar");
        btnLimpiarSin.addActionListener(evt -> btnLimpiarSinActionPerformed(evt));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnAnalizarSin, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addComponent(btnLimpiarSin, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(jScrollPane3)
                                .addGap(18))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(btnAnalizarSin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(8)
                                                .addComponent(btnLimpiarSin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12))
        );

        // --- jPanel3 ---
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resultado de Ejecucion",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 18)));

        txtSalida.setEditable(false);
        txtSalida.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtSalida.setBackground(L_SAL_OUT);
        txtSalida.setColumns(20);
        txtSalida.setRows(6);
        jScrollPane4.setViewportView(txtSalida);

        btnLimpiarSalida.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnLimpiarSalida.setText("Limpiar");
        btnLimpiarSalida.addActionListener(evt -> txtSalida.setText(null));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18)
                                .addComponent(btnLimpiarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(jScrollPane4)
                                .addGap(18))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(btnLimpiarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12))
        );

        // --- Content pane ---
        getContentPane().setLayout(new java.awt.BorderLayout());
        panelCentral = new javax.swing.JPanel();
        panelCentral.setLayout(new javax.swing.BoxLayout(panelCentral, javax.swing.BoxLayout.Y_AXIS));
        panelCentral.add(jPanel1);
        panelCentral.add(jPanel2);
        panelCentral.add(jPanel3);
        javax.swing.JScrollPane scrollPrincipal = new javax.swing.JScrollPane(panelCentral);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(20);
        getContentPane().add(scrollPrincipal, java.awt.BorderLayout.CENTER);

        pack();
        setSize(900, 900);
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivoActionPerformed
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                txtResultado.setText(new String(Files.readAllBytes(chooser.getSelectedFile().toPath())));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnArchivoActionPerformed

    private void btnLimpiarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarLexActionPerformed
        txtAnalizarLex.setText(null);
    }//GEN-LAST:event_btnLimpiarLexActionPerformed

    private void btnLimpiarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSinActionPerformed
        txtAnalizarSin.setText(null);
    }//GEN-LAST:event_btnLimpiarSinActionPerformed

    private void btnAnalizarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarLexActionPerformed
        try {
            analizarLexico();
        } catch (IOException ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalizarLexActionPerformed

    private void btnAnalizarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSinActionPerformed
        analizarSintactico();
    }//GEN-LAST:event_btnAnalizarSinActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new FrmPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalizarLex;
    private javax.swing.JButton btnAnalizarSin;
    private javax.swing.JButton btnArchivo;
    private javax.swing.JButton btnLimpiarLex;
    private javax.swing.JButton btnLimpiarSin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea txtAnalizarLex;
    private javax.swing.JTextArea txtAnalizarSin;
    private javax.swing.JTextArea txtResultado;
    // End of variables declaration//GEN-END:variables

    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea txtSalida;
    private javax.swing.JButton btnLimpiarSalida;
    private javax.swing.JButton btnTema;
    private javax.swing.JPanel panelCentral;
}
