package codigo;
import static codigo.Tokens.*;

%%
%class Lexer
%type Tokens

L=[a-zA-Z_]+
D=[0-9]+
espacio=[ ,\t,\r]+

%{
    public String lexeme;
%}

%%

/* Espacios */
{espacio} {/*Ignore*/}

/* Salto de linea */
( "\n" ) {return Linea;}

/* Comillas */
( "'" ) {lexeme=yytext(); return Comillas;}

/* Tipos de datos */
( vida | mana | energia | puntos | xp | nivel ) 
{lexeme=yytext(); return T_dato;}

/* Texto */
( texto ) {lexeme=yytext(); return Cadena;}

/* CONTROL */
( mision ) {lexeme=yytext(); return If;}
( sino ) {lexeme=yytext(); return Else;}
( repetir ) {lexeme=yytext(); return Do;}
( mientras_jugando ) {lexeme=yytext(); return While;}
( subir_nivel ) {lexeme=yytext(); return For;}

/* BOOLEANOS */
( ganar | perder ) {lexeme=yytext(); return Op_booleano;}

/* OPERADORES */
( mas ) {lexeme=yytext(); return Suma;}
( menos ) {lexeme=yytext(); return Resta;}
( por ) {lexeme=yytext(); return Multiplicacion;}
( entre ) {lexeme=yytext(); return Division;}

/* RELACIONALES */
( mayor | menor | igual | diferente | mayor_igual | menor_igual )
{lexeme=yytext(); return Op_relacional;}

/* ASIGNACIÓN */
( asignar ) {lexeme=yytext(); return Igual;}

/* PARÉNTESIS */
( abrir_mision ) {lexeme=yytext(); return Parentesis_a;}
( cerrar_mision ) {lexeme=yytext(); return Parentesis_c;}

/* BLOQUES */
( iniciar_bloque ) {lexeme=yytext(); return Llave_a;}
( finalizar_bloque ) {lexeme=yytext(); return Llave_c;}

/* INICIO */
( iniciar_juego ) {lexeme=yytext(); return Main;}

/* FIN */
( fin ) {lexeme=yytext(); return P_coma;}

/* IDENTIFICADOR */
{L}({L}|{D})* {lexeme=yytext(); return Identificador;}

/* NÚMERO */
("(-"{D}+")")|{D}+ {lexeme=yytext(); return Numero;}

/* ERROR */
. {return ERROR;}
