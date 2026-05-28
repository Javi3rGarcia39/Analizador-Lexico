package codigo;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.border.TitledBorder;

public class FrmPrincipal extends javax.swing.JFrame {

    // -----------------------------------------------------------------------
    // Colores
    // -----------------------------------------------------------------------
    private static final Color D_BG = new Color(28, 28, 36);
    private static final Color D_PANEL = new Color(36, 36, 48);
    private static final Color D_INPUT = new Color(24, 36, 50);
    private static final Color D_LEX_OUT = new Color(20, 40, 55);
    private static final Color D_SIN_OUT = new Color(32, 32, 44);
    private static final Color D_SAL_OUT = new Color(36, 36, 28);
    private static final Color D_COD_OUT = new Color(28, 38, 52);
    private static final Color D_BTN = new Color(50, 50, 70);
    private static final Color D_FG = new Color(210, 215, 225);
    private static final Color D_TITLE = new Color(100, 180, 255);
    private static final Color D_BORDER = new Color(60, 60, 90);

    private static final Color L_PANEL = Color.WHITE;
    private static final Color L_INPUT = new Color(204, 255, 255);
    private static final Color L_LEX_OUT = new Color(153, 255, 255);
    private static final Color L_SIN_OUT = new Color(204, 204, 204);
    private static final Color L_SAL_OUT = new Color(255, 255, 230);
    private static final Color L_COD_OUT = new Color(230, 240, 255);

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
        Color bg = modoOscuro ? D_BG : new Color(240, 240, 240);
        Color panel = modoOscuro ? D_PANEL : L_PANEL;
        Color input = modoOscuro ? D_INPUT : L_INPUT;
        Color lexOut = modoOscuro ? D_LEX_OUT : L_LEX_OUT;
        Color sinOut = modoOscuro ? D_SIN_OUT : L_SIN_OUT;
        Color salOut = modoOscuro ? D_SAL_OUT : L_SAL_OUT;
        Color codOut = modoOscuro ? D_COD_OUT : L_COD_OUT;
        Color btn = modoOscuro ? D_BTN : new Color(220, 220, 220);
        Color fg = modoOscuro ? D_FG : Color.BLACK;
        Color title = modoOscuro ? D_TITLE : Color.BLACK;
        Color border = modoOscuro ? D_BORDER : Color.GRAY;

        getContentPane().setBackground(bg);
        panelCentral.setBackground(bg);

        jPanel1.setBackground(panel);
        setBorde(jPanel1, "Analizador Lexico", title, border);
        txtResultado.setBackground(input);
        txtResultado.setForeground(fg);
        txtResultado.setCaretColor(fg);
        txtAnalizarLex.setBackground(lexOut);
        txtAnalizarLex.setForeground(fg);
        jScrollPane1.getViewport().setBackground(input);
        jScrollPane2.getViewport().setBackground(lexOut);

        jPanel2.setBackground(panel);
        setBorde(jPanel2, "Analizador Sintactico", title, border);
        txtAnalizarSin.setBackground(sinOut);
        jScrollPane3.getViewport().setBackground(sinOut);

        jPanel3.setBackground(panel);
        setBorde(jPanel3, "Resultado de Ejecucion", title, border);
        txtSalida.setBackground(salOut);
        txtSalida.setForeground(fg);
        jScrollPane4.getViewport().setBackground(salOut);

        jPanel4.setBackground(panel);
        setBorde(jPanel4, "Codigo Intermedio", title, border);
        txtCodigo.setBackground(codOut);
        txtCodigo.setForeground(fg);
        jScrollPane5.getViewport().setBackground(codOut);

        for (javax.swing.JButton b : new javax.swing.JButton[]{
            btnArchivo, btnAnalizarLex, btnLimpiarLex,
            btnAnalizarSin, btnLimpiarSin, btnLimpiarSalida,
            btnGenerarCodigo, btnLimpiarCodigo, btnTema}) {
            b.setBackground(btn);
            b.setForeground(fg);
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
        }
        btnTema.setText(modoOscuro ? "Modo Claro" : "Modo Oscuro");
        repaint();
        revalidate();
    }

    private void setBorde(javax.swing.JPanel p, String t, Color fg, Color linea) {
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(linea, 1), t,
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 18), fg));
    }

    // -----------------------------------------------------------------------
    // Analisis lexico
    // -----------------------------------------------------------------------
    private void analizarLexico() throws IOException {
        String expr = txtResultado.getText();
        LexerCup lexer = new LexerCup(new StringReader(expr));
        StringBuilder resultado = new StringBuilder(String.format("%-7s\t%-8s\t%-20s\t%s\n", "LINEA", "COLUMNA", "SIMBOLO", "LEXEMA"));
        resultado.append("-".repeat(75)).append("\n");

        while (true) {
            Symbol token = lexer.next_token();
            if (token.sym == 0) {
                txtAnalizarLex.setText(resultado.toString());
                return;
            }
            String lex = (token.value != null) ? token.value.toString() : "";
            int linea = token.left + 1;
            int columna = token.right + 1;

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
                case sym.Parentesis_a:
                    tipo = "<Inicio condicion>";
                    break;
                case sym.Parentesis_c:
                    tipo = "<Fin condicion>";
                    break;
                case sym.Op_incremento:
                    tipo = "<Op incremento>";
                    break;
                case sym.Op_atribucion:
                    tipo = "<Op atribucion>";
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
                case sym.ERROR:
                    tipo = "<ERROR LEXICO>";
                    break;
                default:
                    tipo = "<Desconocido>";
                    break;
            }
            resultado.append(String.format("%-7d\t%-8d\t%-20s\t%s\n", linea, columna, tipo, lex));
        }
    }

    // -----------------------------------------------------------------------
    // Verificacion lexica previa
    // -----------------------------------------------------------------------
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
    // Analisis sintactico + semantico + interpretacion
    // -----------------------------------------------------------------------
    private void analizarSintactico() {
        String ST = txtResultado.getText();

        // 1. Verificar lexico
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

        // 2. Verificar sintaxis
        Sintax s = new Sintax(new LexerCup(new StringReader(ST)));
        try {
            s.parse();
        } catch (Exception ex) {
            Symbol err = s.getS();
            String msg;
            if (err != null) {
                int linea = err.left + 1;
                int columna = err.right + 1;
                String lex = err.value != null ? err.value.toString() : "?";
                msg = "Error SINTACTICO en linea " + linea
                        + ", columna " + columna
                        + ". Token inesperado: \"" + lex + "\"";
            } else {
                msg = "Error SINTACTICO desconocido.";
            }
            txtAnalizarSin.setForeground(Color.RED);
            txtAnalizarSin.setText(msg);
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("No se puede ejecutar: hay errores sintacticos.\n\n" + msg);
            return;
        }

        txtAnalizarSin.setForeground(new Color(25, 111, 61));
        txtAnalizarSin.setText("Analisis sintactico correcto.");

        // 3. Analisis semantico
        List<AnalizadorSemantico.ErrorSemantico> erroresSemanticos;
        try {
            erroresSemanticos = AnalizadorSemantico.analizar(ST);
        } catch (Exception ex) {
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("Error al analizar semantica: " + ex.getMessage());
            return;
        }
        if (!erroresSemanticos.isEmpty()) {
            StringBuilder sb = new StringBuilder("Errores semanticos encontrados:\n");
            for (AnalizadorSemantico.ErrorSemantico e : erroresSemanticos) {
                sb.append("  - ").append(e).append("\n");
            }
            txtAnalizarSin.setForeground(new Color(120, 0, 180));
            txtAnalizarSin.setText(sb.toString());
            txtSalida.setForeground(new Color(120, 0, 180));
            txtSalida.setText("No se puede ejecutar: hay errores semanticos.\n\n" + sb);
            return;
        }

        // 4. Detectar variables sin valor
        Interprete interprete = new Interprete();
        Map<String, String> sinValor;
        try {
            sinValor = interprete.detectarVariablesSinValor(ST);
        } catch (Exception ex) {
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("Error al detectar variables: " + ex.getMessage());
            return;
        }

        // 5. Dialogo si hay variables sin valor
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

        // 6. Ejecutar
        try {
            String resultado = interprete.ejecutar(ST, valoresUsuario);
            txtSalida.setForeground(modoOscuro ? D_FG : new Color(30, 30, 30));
            txtSalida.setText("-- Resultado de ejecucion --\n\n" + resultado);
        } catch (Exception ex) {
            txtSalida.setForeground(Color.RED);
            txtSalida.setText("Error en ejecucion: " + ex.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Generacion de codigo intermedio
    // -----------------------------------------------------------------------
    private void generarCodigoIntermedio() {
        String ST = txtResultado.getText();

        // Verificar lexico y sintaxis primero
        try {
            String errorLexico = verificarLexico(ST);
            if (errorLexico != null) {
                txtCodigo.setForeground(new Color(200, 80, 0));
                txtCodigo.setText("No se puede generar: errores lexicos.\n\n" + errorLexico);
                return;
            }
        } catch (Exception ex) {
            txtCodigo.setForeground(Color.RED);
            txtCodigo.setText("Error lexico: " + ex.getMessage());
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
            txtCodigo.setForeground(Color.RED);
            txtCodigo.setText("No se puede generar: errores sintacticos.\n\n" + msg);
            return;
        }

        // Generar codigo intermedio
        try {
            GeneradorCodigoIntermedio gen = new GeneradorCodigoIntermedio();
            String codigo = gen.generar(ST);
            txtCodigo.setForeground(modoOscuro ? D_FG : new Color(20, 40, 80));
            txtCodigo.setText("--- Codigo Intermedio ---\n\n" + codigo);
        } catch (Exception ex) {
            txtCodigo.setForeground(Color.RED);
            txtCodigo.setText("Error al generar codigo intermedio: " + ex.getMessage());
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
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCodigo = new javax.swing.JTextArea();
        btnGenerarCodigo = new javax.swing.JButton();
        btnLimpiarCodigo = new javax.swing.JButton();

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
        txtAnalizarLex.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtAnalizarLex.setColumns(20);
        txtAnalizarLex.setRows(5);
        jScrollPane2.setViewportView(txtAnalizarLex);

        javax.swing.GroupLayout p1 = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(p1);
        p1.setHorizontalGroup(p1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p1.createSequentialGroup()
                        .addGap(18)
                        .addGroup(p1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(btnTema, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(btnAnalizarLex, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(btnLimpiarLex, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(p1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1)
                                .addComponent(jScrollPane2))
                        .addGap(18))
        );
        p1.setVerticalGroup(p1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p1.createSequentialGroup()
                        .addGap(12)
                        .addGroup(p1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(p1.createSequentialGroup()
                                        .addComponent(btnArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6)
                                        .addComponent(btnTema, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8)
                        .addGroup(p1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(p1.createSequentialGroup()
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

        javax.swing.GroupLayout p2 = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(p2);
        p2.setHorizontalGroup(p2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p2.createSequentialGroup()
                        .addGap(18)
                        .addGroup(p2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnAnalizarSin, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(btnLimpiarSin, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane3)
                        .addGap(18))
        );
        p2.setVerticalGroup(p2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p2.createSequentialGroup()
                        .addGap(12)
                        .addGroup(p2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(p2.createSequentialGroup()
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

        javax.swing.GroupLayout p3 = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(p3);
        p3.setHorizontalGroup(p3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p3.createSequentialGroup()
                        .addGap(18)
                        .addComponent(btnLimpiarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane4)
                        .addGap(18))
        );
        p3.setVerticalGroup(p3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p3.createSequentialGroup()
                        .addGap(12)
                        .addGroup(p3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnLimpiarSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12))
        );

        // --- jPanel4: Codigo Intermedio ---
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Codigo Intermedio",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 18)));

        txtCodigo.setEditable(false);
        txtCodigo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtCodigo.setBackground(L_COD_OUT);
        txtCodigo.setColumns(20);
        txtCodigo.setRows(8);
        jScrollPane5.setViewportView(txtCodigo);

        btnGenerarCodigo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnGenerarCodigo.setText("Generar");
        btnGenerarCodigo.addActionListener(evt -> generarCodigoIntermedio());

        btnLimpiarCodigo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnLimpiarCodigo.setText("Limpiar");
        btnLimpiarCodigo.addActionListener(evt -> txtCodigo.setText(null));

        javax.swing.GroupLayout p4 = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(p4);
        p4.setHorizontalGroup(p4.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p4.createSequentialGroup()
                        .addGap(18)
                        .addGroup(p4.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnGenerarCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                .addComponent(btnLimpiarCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane5)
                        .addGap(18))
        );
        p4.setVerticalGroup(p4.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(p4.createSequentialGroup()
                        .addGap(12)
                        .addGroup(p4.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(p4.createSequentialGroup()
                                        .addComponent(btnGenerarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8)
                                        .addComponent(btnLimpiarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12))
        );

        // --- Content pane ---
        getContentPane().setLayout(new java.awt.BorderLayout());
        panelCentral = new javax.swing.JPanel();
        panelCentral.setLayout(new javax.swing.BoxLayout(panelCentral, javax.swing.BoxLayout.Y_AXIS));
        panelCentral.add(jPanel1);
        panelCentral.add(jPanel2);
        panelCentral.add(jPanel3);
        panelCentral.add(jPanel4);
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea txtCodigo;
    private javax.swing.JButton btnGenerarCodigo;
    private javax.swing.JButton btnLimpiarCodigo;
    private javax.swing.JButton btnTema;
    private javax.swing.JPanel panelCentral;

}
