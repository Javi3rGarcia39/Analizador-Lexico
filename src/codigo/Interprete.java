package codigo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java_cup.runtime.Symbol;

/**
 * Interpreta el codigo del lenguaje videojuego. Soporta: declaracion, if/else,
 * while, do-while, for, mostrar, atacar, aumentar/reducir, sumar/restar.
 */
public class Interprete {

    private Map<String, Integer> variables = new HashMap<>();
    private StringBuilder salida = new StringBuilder();
    private List<Symbol> tokens = new ArrayList<>();
    private int pos = 0;

    // -----------------------------------------------------------------------
    // Detectar variables sin valor inicial
    // -----------------------------------------------------------------------
    public Map<String, String> detectarVariablesSinValor(String fuente) throws Exception {
        Map<String, String> resultado = new LinkedHashMap<>();
        LexerCup lexer = new LexerCup(new StringReader(fuente));
        List<Symbol> toks = new ArrayList<>();
        while (true) {
            Symbol t = lexer.next_token();
            toks.add(t);
            if (t.sym == sym.EOF) {
                break;
            }
        }
        for (int i = 0; i < toks.size() - 1; i++) {
            Symbol t = toks.get(i);
            if (t.sym == sym.T_dato) {
                Symbol sig = toks.get(i + 1);
                if (sig.sym == sym.Identificador) {
                    String nombre = sig.value.toString();
                    String tipo = t.value.toString();
                    if (i + 2 < toks.size()) {
                        Symbol dep = toks.get(i + 2);
                        if (dep.sym == sym.P_coma) {
                            resultado.put(nombre, tipo);
                        }
                    }
                }
            }
        }
        return resultado;
    }

    // -----------------------------------------------------------------------
    // Ejecutar con valores del usuario
    // -----------------------------------------------------------------------
    public String ejecutar(String fuente, Map<String, Integer> valoresUsuario) throws Exception {
        variables.clear();
        salida.setLength(0);
        tokens.clear();
        pos = 0;

        if (valoresUsuario != null) {
            variables.putAll(valoresUsuario);
        }

        LexerCup lexer = new LexerCup(new StringReader(fuente));
        while (true) {
            Symbol t = lexer.next_token();
            tokens.add(t);
            if (t.sym == sym.EOF) {
                break;
            }
        }

        consumir(sym.Main);
        consumir(sym.Llave_a);
        ejecutarSentencias();

        return salida.toString();
    }

    // -----------------------------------------------------------------------
    // Bloque de sentencias
    // -----------------------------------------------------------------------
private void ejecutarSentencias() {
    while (pos < tokens.size()) {
        Symbol tok = tokens.get(pos);
        if (tok.sym == sym.EOF || tok.sym == sym.Llave_c) break;

        if      (tok.sym == sym.T_dato)         ejecutarDeclaracion();
        else if (tok.sym == sym.If)             ejecutarIf();
        else if (tok.sym == sym.While)          ejecutarWhile();
        else if (tok.sym == sym.Do)             ejecutarDoWhile();
        else if (tok.sym == sym.For)            ejecutarFor();
        else if (tok.sym == sym.Imprimir)       ejecutarMostrar();
        else if (tok.sym == sym.Accion)         ejecutarAccion();
        else if (tok.sym == sym.Op_incremento)  ejecutarIncrementoDecremento();
        else if (tok.sym == sym.Op_atribucion)  ejecutarAtribucion();
        else if (tok.sym == sym.Suma)           ejecutarAritmetica();
        else if (tok.sym == sym.Resta)          ejecutarAritmetica();
        else if (tok.sym == sym.Multiplicacion) ejecutarAritmetica();
        else if (tok.sym == sym.Division)       ejecutarAritmetica();
        else pos++;
    }
}

    private void ejecutarAritmetica() {
        String op = valorActual();
        pos++; // mas/menos/por/entre
        String izqStr = valorActual();
        pos++; // Numero o Identificador
        String derStr = valorActual();
        pos++; // Numero o Identificador
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) {
            pos++;
        }

        int izq = variables.containsKey(izqStr) ? variables.get(izqStr) : Integer.parseInt(izqStr);
        int der = variables.containsKey(derStr) ? variables.get(derStr) : Integer.parseInt(derStr);

        int resultado;
        String simbolo;
        switch (op) {
            case "mas":
                resultado = izq + der;
                simbolo = "+";
                break;
            case "menos":
                resultado = izq - der;
                simbolo = "-";
                break;
            case "por":
                resultado = izq * der;
                simbolo = "*";
                break;
            case "entre":
                resultado = der != 0 ? izq / der : 0;
                simbolo = "/";
                break;
            default:
                resultado = 0;
                simbolo = "?";
                break;
        }

        salida.append(">> ").append(izq).append(" ").append(simbolo)
                .append(" ").append(der).append(" = ").append(resultado).append("\n");
    }

    // -----------------------------------------------------------------------
    // DECLARACION: T_dato Identificador [Igual Numero] fin
    // -----------------------------------------------------------------------
    private void ejecutarDeclaracion() {
        pos++; // T_dato
        String nombre = valorActual();
        pos++;

        if (pos < tokens.size() && tokens.get(pos).sym == sym.Igual) {
            pos++; // Igual
            int valor = Integer.parseInt(valorActual());
            pos++;
            if (!variables.containsKey(nombre)) {
                variables.put(nombre, valor);
            }
            salida.append(">> ").append(nombre).append(" = ")
                    .append(variables.get(nombre)).append("\n");
        } else {
            int val = variables.getOrDefault(nombre, 0);
            salida.append(">> ").append(nombre).append(" = ").append(val)
                    .append(" (ingresado por usuario)\n");
        }
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) {
            pos++;
        }
    }

    // -----------------------------------------------------------------------
    // INCREMENTO/DECREMENTO: aumentar Identificador fin | reducir Identificador fin
    // -----------------------------------------------------------------------
    private void ejecutarIncrementoDecremento() {
        String op = valorActual();
        pos++; // aumentar | reducir
        String nombre = valorActual();
        pos++; // Identificador
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) {
            pos++;
        }

        int valAntes = variables.getOrDefault(nombre, 0);
        int valDespues;
        if (op.equals("aumentar")) {
            valDespues = valAntes + 1;
            variables.put(nombre, valDespues);
            salida.append(">> ").append(nombre).append("++ => ").append(valDespues).append("\n");
        } else {
            valDespues = valAntes - 1;
            variables.put(nombre, valDespues);
            salida.append(">> ").append(nombre).append("-- => ").append(valDespues).append("\n");
        }
    }

    // -----------------------------------------------------------------------
    // ATRIBUCION: sumar Identificador Numero fin | restar Identificador Numero fin
    // -----------------------------------------------------------------------
    private void ejecutarAtribucion() {
        String op = valorActual();
        pos++; // sumar | restar
        String nombre = valorActual();
        pos++; // Identificador
        int valor = Integer.parseInt(valorActual());
        pos++; // Numero
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) {
            pos++;
        }

        int valAntes = variables.getOrDefault(nombre, 0);
        int valDespues;
        if (op.equals("sumar")) {
            valDespues = valAntes + valor;
            variables.put(nombre, valDespues);
            salida.append(">> ").append(nombre).append(" += ").append(valor)
                    .append(" => ").append(valDespues).append("\n");
        } else {
            valDespues = valAntes - valor;
            variables.put(nombre, valDespues);
            salida.append(">> ").append(nombre).append(" -= ").append(valor)
                    .append(" => ").append(valDespues).append("\n");
        }
    }

    // -----------------------------------------------------------------------
    // IF
    // -----------------------------------------------------------------------
    private void ejecutarIf() {
        pos++; // If
        pos++; // Parentesis_a
        boolean condicion = evaluarCondicion();
        pos++; // Parentesis_c
        pos++; // Llave_a

        if (condicion) {
            salida.append(">> Ejecutando bloque si...\n");
            ejecutarSentencias();
            pos++; // Llave_c
            if (pos < tokens.size() && tokens.get(pos).sym == sym.Else) {
                pos++;
                pos++; // Else + Llave_a
                saltarBloque();
            }
        } else {
            saltarBloque();
            if (pos < tokens.size() && tokens.get(pos).sym == sym.Else) {
                pos++;
                pos++; // Else + Llave_a
                salida.append(">> Ejecutando bloque sino...\n");
                ejecutarSentencias();
                pos++; // Llave_c
            } else {
                salida.append(">> Condicion falsa, bloque si omitido.\n");
            }
        }
    }

    // -----------------------------------------------------------------------
    // WHILE
    // -----------------------------------------------------------------------
    private void ejecutarWhile() {
        pos++; // consumir While
        pos++; // consumir Parentesis_a

        int posInicioCondicion = pos;

        // Primera evaluacion
        boolean condicion = evaluarCondicion();
        pos++; // consumir Parentesis_c
        pos++; // consumir Llave_a

        int posInicioCuerpo = pos;
        int posDesp = -1; // posicion despues del Llave_c del while

        if (!condicion) {
            salida.append(">> Mientras: condicion falsa desde el inicio.\n");
            saltarBloque();
            return;
        }

        int iteraciones = 0;
        while (condicion && iteraciones < 1000) {
            salida.append(">> Iteracion ").append(iteraciones + 1).append("\n");
            pos = posInicioCuerpo;
            ejecutarSentencias();
            pos++; // consumir Llave_c
            posDesp = pos; // guardar posicion despues del bloque

            pos = posInicioCondicion;
            condicion = evaluarCondicion();
            iteraciones++;
        }

        // Al salir, restaurar pos a despues del bloque while
        if (posDesp != -1) {
            pos = posDesp;
        }

        if (iteraciones >= 1000) {
            salida.append(">> Mientras: limite de 1000 iteraciones alcanzado.\n");
        }
    }

    // -----------------------------------------------------------------------
    // DO-WHILE
    // -----------------------------------------------------------------------
    private void ejecutarDoWhile() {
        pos++; // Do
        pos++; // Llave_a
        int posCuerpo = pos;
        boolean condicion = true;
        int iteraciones = 0;

        while (condicion && iteraciones < 1000) {
            pos = posCuerpo;
            salida.append(">> Iteracion hacer ").append(iteraciones + 1).append("\n");
            ejecutarSentencias();
            pos++; // Llave_c
            pos++; // While
            pos++; // Parentesis_a
            condicion = evaluarCondicion();
            pos++; // Parentesis_c
            pos++; // P_coma
            iteraciones++;
        }
        if (iteraciones >= 1000) {
            salida.append(">> Hacer-mientras: limite de 1000 iteraciones alcanzado.\n");
        }
    }

    // -----------------------------------------------------------------------
    // FOR
    // -----------------------------------------------------------------------
    private void ejecutarFor() {
        pos++; // consumir For
        pos++; // consumir Parentesis_a

        int posInicioCondicion = pos;

        boolean condicion = evaluarCondicion();
        pos++; // consumir Parentesis_c
        pos++; // consumir Llave_a

        int posInicioCuerpo = pos;
        int posDesp = -1;

        if (!condicion) {
            salida.append(">> Para: condicion falsa desde el inicio.\n");
            saltarBloque();
            return;
        }

        int iteraciones = 0;
        while (condicion && iteraciones < 1000) {
            salida.append(">> Para iteracion ").append(iteraciones + 1).append("\n");
            pos = posInicioCuerpo;
            ejecutarSentencias();
            pos++; // consumir Llave_c
            posDesp = pos;

            pos = posInicioCondicion;
            condicion = evaluarCondicion();
            iteraciones++;
        }

        if (posDesp != -1) {
            pos = posDesp;
        }

        if (iteraciones >= 1000) {
            salida.append(">> Para: limite de 1000 iteraciones alcanzado.\n");
        }
    }

    // -----------------------------------------------------------------------
    // MOSTRAR
    // -----------------------------------------------------------------------
    private void ejecutarMostrar() {
        pos++; // Imprimir
        pos++; // Comilla apertura
        StringBuilder texto = new StringBuilder();

        while (pos < tokens.size()
                && tokens.get(pos).sym != sym.Comillas
                && tokens.get(pos).sym != sym.P_coma
                && tokens.get(pos).sym != sym.EOF) {
            String val = valorActual();
            if (tokens.get(pos).sym == sym.Identificador && variables.containsKey(val)) {
                texto.append(val).append("=").append(variables.get(val));
            } else {
                texto.append(val);
            }
            texto.append(" ");
            pos++;
        }

        if (pos < tokens.size() && tokens.get(pos).sym == sym.Comillas) {
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) {
            pos++;
        }

        salida.append(">> ").append(texto.toString().trim()).append("\n");
    }

    // -----------------------------------------------------------------------
    // ACCION
    // -----------------------------------------------------------------------
    private void ejecutarAccion() {
        pos++; // Accion
        String objetivo = valorActual();
        pos++;
        if (pos < tokens.size() && tokens.get(pos).sym == sym.P_coma) {
            pos++;
        }
        salida.append(">> accion sobre: ").append(objetivo).append("\n");
    }

    // -----------------------------------------------------------------------
    // Evaluar condicion
    // -----------------------------------------------------------------------
    private boolean evaluarCondicion() {
        Symbol tok = tokens.get(pos);

        if (tok.sym == sym.Op_booleano) {
            String val = valorActual();
            pos++;
            boolean r = val.equals("ganar");
            salida.append(">> ").append(val).append(" => ")
                    .append(r ? "verdadero" : "falso").append("\n");
            return r;
        }

        String izq = valorActual();
        pos++;
        String op = valorActual();
        pos++;
        String der = valorActual();
        pos++;

        int valIzq = variables.getOrDefault(izq, 0);
        int valDer;
        try {
            valDer = Integer.parseInt(der);
        } catch (NumberFormatException e) {
            valDer = variables.getOrDefault(der, 0);
        }

        boolean resultado = evaluar(valIzq, op, valDer);
        salida.append(">> ").append(izq).append("(").append(valIzq).append(")")
                .append(" ").append(op).append(" ").append(valDer)
                .append(" => ").append(resultado ? "verdadero" : "falso").append("\n");
        return resultado;
    }

    private boolean evaluar(int a, String op, int b) {
        switch (op) {
            case "mayor":
                return a > b;
            case "menor":
                return a < b;
            case "igual":
                return a == b;
            case "diferente":
                return a != b;
            case "mayor_igual":
                return a >= b;
            case "menor_igual":
                return a <= b;
            default:
                return false;
        }
    }

    // -----------------------------------------------------------------------
    // Saltar bloque sin ejecutar
    // -----------------------------------------------------------------------
    private void saltarBloque() {
        int nivel = 1;
        while (pos < tokens.size() && nivel > 0) {
            if (tokens.get(pos).sym == sym.Llave_a) {
                nivel++;
            } else if (tokens.get(pos).sym == sym.Llave_c) {
                nivel--;
            }
            pos++;
        }
    }

    private void consumir(int tipo) {
        if (pos < tokens.size() && tokens.get(pos).sym == tipo) {
            pos++;
        }
    }

    private String valorActual() {
        if (pos >= tokens.size()) {
            return "";
        }
        Symbol t = tokens.get(pos);
        return t.value != null ? t.value.toString() : "";
    }
}
