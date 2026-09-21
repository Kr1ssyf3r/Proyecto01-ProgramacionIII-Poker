import juego.BotAgresivo;
import juego.BotConservador;
import juego.JuegoPoker;
import model.AccionPoker;
import model.JugadorBot;
import model.JugadorHumano;
import model.ResultadoRonda;

import java.util.List;

/**
 * Prueba el caso en que el humano apuesta todas sus fichas al empezar la ronda: la mano debe llegar
 * al showdown, repartir el bote y conservar las fichas (antes el humano quedaba fuera y perdía el bote).
 */
public class PruebaAllIn {

    /**
     * Repite 300 rondas con el humano apostando 500 fichas y verifica el resultado.
     * @param args no se usan.
     */
    public static void main(String[] args) {
        int humanoGana = 0, otroGana = 0, fallos = 0;
        for (int i = 0; i < 300; i++) {
            JugadorHumano humano = new JugadorHumano("Humano", 500);
            JugadorBot conservador = new JugadorBot("Bot Conservador", 500, new BotConservador());
            JugadorBot agresivo = new JugadorBot("Bot Agresivo", 500, new BotAgresivo());
            JuegoPoker juego = new JuegoPoker(List.of(humano, conservador, agresivo));
            try {
                juego.iniciarRonda();
                juego.procesarAccion(humano, AccionPoker.RAISE, 500);
                ResultadoRonda resultado = juego.isRondaTerminada() && "SHOWDOWN".equals(juego.getFaseActual())
                        ? juego.resolverRonda() : null;
                int total = humano.getSaldoFichas() + conservador.getSaldoFichas() + agresivo.getSaldoFichas();
                if (resultado == null || total != 1500 || juego.getBote() != 0) fallos++;
                else if (resultado.getGanador() == humano) humanoGana++;
                else otroGana++;
            } catch (RuntimeException e) {
                fallos++;
            }
        }
        System.out.println("All-in del humano en 300 rondas: gana el humano " + humanoGana
                + ", gana otro " + otroGana + ", fallos " + fallos);
        System.out.println(fallos == 0 ? "RESULTADO: OK" : "RESULTADO: FALLO");
        if (fallos != 0) System.exit(1);
    }
}
