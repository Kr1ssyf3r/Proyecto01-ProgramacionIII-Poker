import model.Carta;
import model.ManoPoker;
import model.Mazo;

import java.util.ArrayList;
import java.util.List;

/**
 * Reparte pares de manos de 7 cartas al azar y, por cada par, imprime las cartas, la combinación de
 * cada mano y quién gana según ManoPoker. La salida se contrasta con comparar_evaluador.py.
 */
public class GeneradorManos {

    /**
     * Genera la cantidad indicada de pares de manos.
     * @param args cantidad de pares de manos que se generan.
     */
    public static void main(String[] args) {
        int cantidad = Integer.parseInt(args[0]);
        for (int i = 0; i < cantidad; i++) {
            Mazo<Carta> mazo = Mazo.crearMazoPoker();
            List<Carta> a = new ArrayList<>(mazo.repartir(7));
            List<Carta> b = new ArrayList<>(mazo.repartir(7));
            ManoPoker manoA = new ManoPoker(a, true);
            ManoPoker manoB = new ManoPoker(b, true);
            System.out.println(texto(a) + "| " + texto(b) + "| " + manoA.evaluar() + " " + manoB.evaluar()
                    + " " + Integer.signum(manoA.compararCon(manoB)));
        }
    }

    /**
     * Convierte una lista de cartas en texto, por ejemplo "14P 7T ".
     * @param cartas cartas a convertir.
     * @return texto con el valor y la inicial del palo de cada carta.
     */
    private static String texto(List<Carta> cartas) {
        StringBuilder sb = new StringBuilder();
        for (Carta c : cartas) sb.append(c.getValor()).append(c.getPalo().name().charAt(0)).append(' ');
        return sb.toString();
    }
}
