package codigo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java_cup.runtime.Symbol;

/**
 * Analizador semantico: recorre los tokens y detecta el uso de identificadores
 * que no fueron declarados.
 *
 * Retorna una lista de errores. Si esta vacia, no hay errores.
 */
public class AnalizadorSemantico {

    public static class ErrorSemantico {

        public final String mensaje;
        public final int linea;
        public final int columna;

        public ErrorSemantico(String mensaje, int linea, int columna) {
            this.mensaje = mensaje;
            this.linea = linea;
            this.columna = columna;
        }

        @Override
        public String toString() {
            return "Error SEMANTICO en linea " + linea
                    + ", columna " + columna
                    + ". Variable no declarada: \"" + mensaje + "\"";
        }
    }

    public static List<ErrorSemantico> analizar(String fuente) throws Exception {
        List<ErrorSemantico> errores = new ArrayList<>();
        List<Symbol> tokens = tokenizar(fuente);

        // Paso 1: recolectar todas las variables declaradas
        Set<String> declaradas = new LinkedHashSet<>();
        for (int i = 0; i < tokens.size() - 1; i++) {
            Symbol t = tokens.get(i);
            if (t.sym == sym.T_dato) {
                Symbol sig = tokens.get(i + 1);
                // T_dato Identificador ...
                if (sig.sym == sym.Identificador) {
                    declaradas.add(sig.value.toString());
                } // T_dato Op_incremento Identificador ... (ej: vida ++ salud)
                else if (sig.sym == sym.Op_incremento && i + 2 < tokens.size()) {
                    Symbol sig2 = tokens.get(i + 2);
                    if (sig2.sym == sym.Identificador) {
                        declaradas.add(sig2.value.toString());
                    }
                }
            }
        }

        // Paso 2: verificar usos
        for (int i = 0; i < tokens.size(); i++) {
            Symbol t = tokens.get(i);

            // Condicion: Identificador Op_relacional Numero/Identificador
            if (t.sym == sym.Identificador) {
                // Ver contexto: si el anterior es Parentesis_a o el siguiente es Op_relacional
                // es parte de una condicion
                boolean esUso = false;

                if (i > 0) {
                    int prevSym = tokens.get(i - 1).sym;
                    // despues de abrir condicion
                    if (prevSym == sym.Parentesis_a) {
                        esUso = true;
                    }
                }
                if (i + 1 < tokens.size()) {
                    int nextSym = tokens.get(i + 1).sym;
                    // antes de operador relacional
                    if (nextSym == sym.Op_relacional) {
                        esUso = true;
                    }
                    // parte derecha de condicion: Op_relacional Identificador
                    if (i > 0 && tokens.get(i - 1).sym == sym.Op_relacional) {
                        esUso = true;
                    }
                }

                // NO marcar como uso si el token anterior es Comillas
// (significa que es texto dentro de mostrar ' texto ')
// Solo marcar si el token siguiente NO es Comillas
                if (i > 0 && tokens.get(i - 1).sym == sym.Comillas) {
                    // es texto literal, no es una variable
                    esUso = false;
                }

                // reducir/aumentar Identificador fin
                if (i > 0 && tokens.get(i - 1).sym == sym.Op_incremento) {
                    esUso = true;
                }

                // sumar/restar Identificador Numero
                if (i > 0 && tokens.get(i - 1).sym == sym.Op_atribucion) {
                    esUso = true;
                }

                // atacar Identificador
                if (i > 0 && tokens.get(i - 1).sym == sym.Accion) {
                    esUso = true;
                }

                // mas/menos/por/entre Identificador Identificador
                if (i > 0) {
                    int prevSym = tokens.get(i - 1).sym;
                    if (prevSym == sym.Suma || prevSym == sym.Resta
                            || prevSym == sym.Multiplicacion || prevSym == sym.Division) {
                        esUso = true;
                    }
                }
                if (i + 1 < tokens.size()) {
                    int nextSym = tokens.get(i + 1).sym;
                    if (nextSym == sym.Suma || nextSym == sym.Resta
                            || nextSym == sym.Multiplicacion || nextSym == sym.Division) {
                        // segundo operando de aritmetica
                    }
                }
                // segundo operando de aritmetica: Numero/Identificador despues de primer operando
                if (i > 1) {
                    int prevPrevSym = tokens.get(i - 2).sym;
                    if (prevPrevSym == sym.Suma || prevPrevSym == sym.Resta
                            || prevPrevSym == sym.Multiplicacion || prevPrevSym == sym.Division) {
                        esUso = true;
                    }
                }

                if (esUso) {
                    String nombre = t.value.toString();
                    if (!declaradas.contains(nombre)) {
                        int linea = t.left + 1;
                        int columna = t.right + 1;
                        errores.add(new ErrorSemantico(nombre, linea, columna));
                    }
                }
            }
        }

        return errores;
    }

    private static List<Symbol> tokenizar(String fuente) throws Exception {
        List<Symbol> lista = new ArrayList<>();
        LexerCup lexer = new LexerCup(new StringReader(fuente));
        while (true) {
            Symbol t = lexer.next_token();
            lista.add(t);
            if (t.sym == sym.EOF) {
                break;
            }
        }
        return lista;
    }
}
