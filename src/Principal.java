
import java.io.File;

public class Principal {

    public static void main(String[] args) {

        try {

            // ✅ Rutas RELATIVAS (IMPORTANTE)
            String ruta1 = "src/codigo/Lexer.flex";
            String ruta2 = "src/codigo/LexerCup.flex";
            String[] rutaS = { "-parser", "Sintax", "-destdir", "src/codigo", "src/codigo/Sintax.cup" };

            // ✅ Generar Lexer normal
            File archivo1 = new File(ruta1);
            JFlex.Main.generate(archivo1);

            // ✅ Generar LexerCup
            File archivo2 = new File(ruta2);
            JFlex.Main.generate(archivo2);

            // ✅ Generar CUP (Parser + sym)
            java_cup.Main.main(rutaS);

            System.out.println("✅ Archivos generados correctamente");

        } catch (Exception e) {
            System.out.println("❌ Error al generar archivos");
            e.printStackTrace();
        }
    }
}