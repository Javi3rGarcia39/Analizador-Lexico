package codigo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.Symbol;

/**
 * Genera codigo intermedio de tres direcciones a partir del lenguaje videojuego.
 * El formato es similar al bytecode conceptual de Java:
 *   - Asignaciones:  variable = valor
 *   - Temporales:    t0 = a + b
 *   - Etiquetas:     L0:
 *   - Saltos:        goto L0
 *   - Condicionales: if var op val goto L1
 *   - Print:         print texto
 *   - Call:          call objetivo
 */
public class GeneradorCodigoIntermedio {

    private List<Symbol> tokens = new ArrayList<>();
    private int pos = 0;
    private StringBuilder codigo = new StringBuilder();
    private int contadorEtiqueta = 0;
    private int contadorTemporal = 0;

    // -----------------------------------------------------------------------
    // Punto de entrada
    // -----------------------------------------------------------------------
    public String generar(String fuente) throws Exception {
        tokens.clear();
        pos = 0;
        codigo.setLength(0);
        contadorEtiqueta = 0;
        contadorTemporal = 0;

        LexerCup lexer = new LexerCup(new StringReader(fuente));
        while (true) {
            Symbol t = lexer.next_token();
            tokens.add(t);
            if (t.sym == sym.EOF) break;
        }

        consumir(sym.Main);
        consumir(sym.Llave_a);
        generarSentencias(0);

        return codigo.toString();
    }

    // -----------------------------------------------------------------------
    // Generar sentencias de un bloque
    // indent: nivel de indentacion
    // -----------------------------------------------------------------------
    private void generarSentencias(int indent) {
        while (pos < tokens.size()) {
            Symbol tok = tokens.get(pos);
            if (tok.sym == sym.EOF || tok.sym == sym.Llave_c) break;

            if      (tok.sym == sym.T_dato)        generarDeclaracion(indent);
            else if (tok.sym == sym.If)             generarIf(indent);
            else if (tok.sym == sym.While)          generarWhile(indent);
            else if (tok.sym == sym.Do)             generarDoWhile(indent);
            else if (tok.sym == sym.For)            generarFor(indent);
            else if (tok.sym == sym.Imprimir)       generarMostrar(indent);
            else if (tok.sym == sym.Accion)         generarAccion(indent);
            else if (tok.sym == sym.Op_incremento)  generarIncrementoDecremento(indent);
            else if (tok.sym == sym.Op_atribucion)  generarAtribucion(indent);
            else if (tok.sym == sym.Suma ||
                     tok.sym == sym.Resta ||
                     tok.sym == sym.Multiplicacion ||
                     tok.sym == sym.Division)       generarAritmetica(indent);
            else pos++;
        }
    }

    // -----------------------------------------------------------------------
    // DECLARACION: T_dato Identificador [Igual Numero] fin
    // -----------------------------------------------------------------------
    private void generarDeclaracion(int indent) {
        pos++; // T_dato
        String nombre = valorActual(); pos++;

        if (pos < tokens.size() && tokens.get(pos).sym == sym.Igual) {
            pos++; // Igual
            String valor = valorActual(); pos++;
            emitir(indent, nombre + " = " + valor);
        } else {
            emitir(indent, nombre + " = 0    // sin valor inicial");
        }
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) pos++;
    }

    // -----------------------------------------------------------------------
    // INCREMENTO/DECREMENTO: aumentar Identificador fin | reducir Identificador fin
    // -----------------------------------------------------------------------
    private void generarIncrementoDecremento(int indent) {
        String op = valorActual(); pos++;    // aumentar | reducir
        String nombre = valorActual(); pos++; // Identificador
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) pos++;

        if (op.equals("aumentar")) {
            emitir(indent, nombre + " = " + nombre + " + 1");
        } else {
            emitir(indent, nombre + " = " + nombre + " - 1");
        }
    }

    // -----------------------------------------------------------------------
    // ATRIBUCION: sumar Identificador Numero fin | restar Identificador Numero fin
    // -----------------------------------------------------------------------
    private void generarAtribucion(int indent) {
        String op    = valorActual(); pos++; // sumar | restar
        String nombre = valorActual(); pos++; // Identificador
        String valor  = valorActual(); pos++; // Numero
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) pos++;

        if (op.equals("sumar")) {
            emitir(indent, nombre + " = " + nombre + " + " + valor);
        } else {
            emitir(indent, nombre + " = " + nombre + " - " + valor);
        }
    }

    // -----------------------------------------------------------------------
    // ARITMETICA: mas/menos/por/entre N1 N2 fin
    // -----------------------------------------------------------------------
    private void generarAritmetica(int indent) {
        String op  = valorActual(); pos++; // mas | menos | por | entre
        String izq = valorActual(); pos++;
        String der = valorActual(); pos++;
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) pos++;

        String opSimb;
        switch (op) {
            case "mas":    opSimb = "+"; break;
            case "menos":  opSimb = "-"; break;
            case "por":    opSimb = "*"; break;
            case "entre":  opSimb = "/"; break;
            default:       opSimb = "?";
        }
        String temp = nuevaTemporal();
        emitir(indent, temp + " = " + izq + " " + opSimb + " " + der);
    }

    // -----------------------------------------------------------------------
    // IF: mision ( COND ) { SENT } [sino { SENT }]
    // -----------------------------------------------------------------------
    private void generarIf(int indent) {
        pos++; // If
        pos++; // Parentesis_a

        String[] condParts = leerCondicion();

        pos++; // Parentesis_c
        pos++; // Llave_a

        String etiquetaSi  = nuevaEtiqueta(); // L_si
        String etiquetaFin = nuevaEtiqueta(); // L_fin

        // Revisar si hay sino para generar salto correcto
        // Emitir salto condicional
        emitir(indent, "if " + condParts[0] + " " + condParts[1] + " " + condParts[2]
                     + " goto " + etiquetaSi);

        // Ver si hay sino
        // Guardamos pos para poder lookahead
        int posTras = posTrasPorVenir(pos); // pos despues del Llave_c del if

        boolean haySino = posTras < tokens.size()
                        && tokens.get(posTras).sym == sym.Else;

        String etiquetaSino = haySino ? nuevaEtiqueta() : etiquetaFin;

        emitir(indent, "goto " + etiquetaSino);
        emitirEtiqueta(etiquetaSi + ":");

        // Cuerpo si
        generarSentencias(indent + 1);
        pos++; // Llave_c del if

        if (haySino) {
            emitir(indent + 1, "goto " + etiquetaFin);
            emitirEtiqueta(etiquetaSino + ":");
            pos++; // Else
            pos++; // Llave_a
            generarSentencias(indent + 1);
            pos++; // Llave_c del sino
        }

        emitirEtiqueta(etiquetaFin + ":");
    }

    // -----------------------------------------------------------------------
    // WHILE: mientras_jugando ( COND ) { SENT }
    // -----------------------------------------------------------------------
    private void generarWhile(int indent) {
        pos++; // While
        pos++; // Parentesis_a

        String etiquetaInicio = nuevaEtiqueta();
        String etiquetaCuerpo = nuevaEtiqueta();
        String etiquetaFin    = nuevaEtiqueta();

        emitirEtiqueta(etiquetaInicio + ":");

        String[] condParts = leerCondicion();
        pos++; // Parentesis_c
        pos++; // Llave_a

        emitir(indent, "if " + condParts[0] + " " + condParts[1] + " " + condParts[2]
                     + " goto " + etiquetaCuerpo);
        emitir(indent, "goto " + etiquetaFin);
        emitirEtiqueta(etiquetaCuerpo + ":");

        generarSentencias(indent + 1);
        pos++; // Llave_c

        emitir(indent + 1, "goto " + etiquetaInicio);
        emitirEtiqueta(etiquetaFin + ":");
    }

    // -----------------------------------------------------------------------
    // DO-WHILE: repetir { SENT } mientras_jugando ( COND ) fin
    // -----------------------------------------------------------------------
    private void generarDoWhile(int indent) {
        pos++; // Do
        pos++; // Llave_a

        String etiquetaInicio = nuevaEtiqueta();
        String etiquetaFin    = nuevaEtiqueta();

        emitirEtiqueta(etiquetaInicio + ":");
        generarSentencias(indent + 1);
        pos++; // Llave_c

        pos++; // While
        pos++; // Parentesis_a

        String[] condParts = leerCondicion();
        pos++; // Parentesis_c
        pos++; // P_coma

        emitir(indent, "if " + condParts[0] + " " + condParts[1] + " " + condParts[2]
                     + " goto " + etiquetaInicio);
        emitirEtiqueta(etiquetaFin + ":");
    }

    // -----------------------------------------------------------------------
    // FOR: subir_nivel ( COND ) { SENT }
    // -----------------------------------------------------------------------
    private void generarFor(int indent) {
        pos++; // For
        pos++; // Parentesis_a

        String etiquetaInicio = nuevaEtiqueta();
        String etiquetaCuerpo = nuevaEtiqueta();
        String etiquetaFin    = nuevaEtiqueta();

        emitirEtiqueta(etiquetaInicio + ":");

        String[] condParts = leerCondicion();
        pos++; // Parentesis_c
        pos++; // Llave_a

        emitir(indent, "if " + condParts[0] + " " + condParts[1] + " " + condParts[2]
                     + " goto " + etiquetaCuerpo);
        emitir(indent, "goto " + etiquetaFin);
        emitirEtiqueta(etiquetaCuerpo + ":");

        generarSentencias(indent + 1);
        pos++; // Llave_c

        emitir(indent + 1, "goto " + etiquetaInicio);
        emitirEtiqueta(etiquetaFin + ":");
    }

    // -----------------------------------------------------------------------
    // MOSTRAR: mostrar ' texto_o_id ' fin
    // -----------------------------------------------------------------------
    private void generarMostrar(int indent) {
        pos++; // Imprimir
        pos++; // Comilla apertura
        StringBuilder texto = new StringBuilder();

        while (pos < tokens.size()
               && tokens.get(pos).sym != sym.Comillas
               && tokens.get(pos).sym != sym.P_coma
               && tokens.get(pos).sym != sym.EOF) {
            texto.append(valorActual()).append(" ");
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).sym == sym.Comillas) pos++;
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma)   pos++;

        emitir(indent, "print " + texto.toString().trim());
    }

    // -----------------------------------------------------------------------
    // ACCION: atacar Identificador fin
    // -----------------------------------------------------------------------
    private void generarAccion(int indent) {
        pos++; // Accion
        String objetivo = valorActual(); pos++;
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) pos++;
        emitir(indent, "call " + objetivo);
    }

    // -----------------------------------------------------------------------
    // Leer condicion sin avanzar mas alla de Parentesis_c
    // Devuelve [izq, op, der] como Strings
    // -----------------------------------------------------------------------
    private String[] leerCondicion() {
        Symbol tok = tokens.get(pos);

        // ganar / perder
        if (tok.sym == sym.Op_booleano) {
            String val = valorActual(); pos++;
            // Emitir como asignacion a temporal
            String t = nuevaTemporal();
            emitir(0, t + " = " + (val.equals("ganar") ? "true" : "false"));
            return new String[]{t, "==", "true"};
        }

        String izq = valorActual(); pos++;
        String op  = valorActual(); pos++;
        String der = valorActual(); pos++;

        String opSimb = traducirOp(op);
        return new String[]{izq, opSimb, der};
    }

    private String traducirOp(String op) {
        switch (op) {
            case "mayor":       return ">";
            case "menor":       return "<";
            case "igual":       return "==";
            case "diferente":   return "!=";
            case "mayor_igual": return ">=";
            case "menor_igual": return "<=";
            default:            return op;
        }
    }

    // -----------------------------------------------------------------------
    // Calcular posicion tras el Llave_c de cierre del bloque actual
    // sin mover pos real
    // -----------------------------------------------------------------------
    private int posTrasPorVenir(int desde) {
        int nivel = 1;
        int i = desde;
        while (i < tokens.size() && nivel > 0) {
            if (tokens.get(i).sym == sym.Llave_a) nivel++;
            else if (tokens.get(i).sym == sym.Llave_c) nivel--;
            i++;
        }
        return i; // apunta al token despues del Llave_c
    }

    // -----------------------------------------------------------------------
    // Helpers de emision
    // -----------------------------------------------------------------------
    private void emitir(int indent, String linea) {
        for (int i = 0; i < indent; i++) codigo.append("    ");
        codigo.append(linea).append("\n");
    }

    private void emitirEtiqueta(String etiqueta) {
        codigo.append(etiqueta).append("\n");
    }

    private String nuevaEtiqueta() {
        return "L" + contadorEtiqueta++;
    }

    private String nuevaTemporal() {
        return "t" + contadorTemporal++;
    }

    // -----------------------------------------------------------------------
    // Helpers de tokens
    // -----------------------------------------------------------------------
    private void consumir(int tipo) {
        if (pos < tokens.size() && tokens.get(pos).sym == tipo) pos++;
    }

    private String valorActual() {
        if (pos >= tokens.size()) return "";
        Symbol t = tokens.get(pos);
        return t.value != null ? t.value.toString() : "";
    }
}