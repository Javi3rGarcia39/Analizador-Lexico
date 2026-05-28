package codigo;
import java_cup.runtime.Symbol;

%%
%class LexerCup
%type java_cup.runtime.Symbol
%cup
%full
%line
%column

L=[a-zA-Z_]+
D=[0-9]+
espacio=[ \t\r\n]+

%%

/* Espacios */
{espacio} {}

/* Main */
(iniciar_juego) { return new Symbol(sym.Main, yyline, yycolumn, yytext()); }

/* Bloques */
(iniciar_bloque)   { return new Symbol(sym.Llave_a, yyline, yycolumn, yytext()); }
(finalizar_bloque) { return new Symbol(sym.Llave_c, yyline, yycolumn, yytext()); }

/* Tipos */
(vida|mana|energia) { return new Symbol(sym.T_dato, yyline, yycolumn, yytext()); }

/* Asignacion */
(asignar) { return new Symbol(sym.Igual, yyline, yycolumn, yytext()); }

/* Fin sentencia */
(fin) { return new Symbol(sym.P_coma, yyline, yycolumn, yytext()); }

/* Condicionales */
(mision) { return new Symbol(sym.If,   yyline, yycolumn, yytext()); }
(sino)   { return new Symbol(sym.Else, yyline, yycolumn, yytext()); }

/* Bucles */
(mientras_jugando) { return new Symbol(sym.While, yyline, yycolumn, yytext()); }
(repetir)          { return new Symbol(sym.Do,    yyline, yycolumn, yytext()); }
(subir_nivel)      { return new Symbol(sym.For,   yyline, yycolumn, yytext()); }

/* Parentesis */
(abrir_mision)  { return new Symbol(sym.Parentesis_a, yyline, yycolumn, yytext()); }
(cerrar_mision) { return new Symbol(sym.Parentesis_c, yyline, yycolumn, yytext()); }

/* Relacional */
(mayor|menor|igual|diferente|mayor_igual|menor_igual)
    { return new Symbol(sym.Op_relacional, yyline, yycolumn, yytext()); }

/* Booleano */
(ganar|perder) { return new Symbol(sym.Op_booleano, yyline, yycolumn, yytext()); }

/* Incremento/Decremento */
(aumentar|reducir) { return new Symbol(sym.Op_incremento, yyline, yycolumn, yytext()); }

/* Atribucion con valor */
(sumar|restar) { return new Symbol(sym.Op_atribucion, yyline, yycolumn, yytext()); }

/* Operadores aritmeticos */
"mas"   { return new Symbol(sym.Suma,           yyline, yycolumn, yytext()); }
"menos" { return new Symbol(sym.Resta,          yyline, yycolumn, yytext()); }
"por"   { return new Symbol(sym.Multiplicacion, yyline, yycolumn, yytext()); }
"entre" { return new Symbol(sym.Division,       yyline, yycolumn, yytext()); }

/* Mostrar */
(mostrar) { return new Symbol(sym.Imprimir, yyline, yycolumn, yytext()); }

/* Accion */
(atacar) { return new Symbol(sym.Accion, yyline, yycolumn, yytext()); }

/* Comillas */
("'") { return new Symbol(sym.Comillas, yyline, yycolumn, yytext()); }

/* Numero */
{D}+ { return new Symbol(sym.Numero, yyline, yycolumn, yytext()); }

/* Identificador */
{L}({L}|{D})* { return new Symbol(sym.Identificador, yyline, yycolumn, yytext()); }

/* Error */
. { return new Symbol(sym.ERROR, yyline, yycolumn, yytext()); }
