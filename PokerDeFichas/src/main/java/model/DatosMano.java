package model;
import java.util.List;

/**
 * Datos de una mano terminada, listos para guardarse en la base de datos.
 * Usa nombres de jugador (son únicos en la tabla jugador) en lugar de ids.
 * @param ganador nombre del jugador que ganó la mano.
 * @param bote fichas que ganó el ganador.
 * @param combinacion combinación ganadora (o SIN_SHOWDOWN si todos se retiraron).
 * @param participaciones fichas de cada jugador antes y después de la mano.
 * @param acciones jugadas de la mano, en el orden en que ocurrieron.
 */
public record DatosMano(String ganador, int bote, String combinacion,
                        List<Participacion> participaciones, List<Jugada> acciones) {

    /**
     * Fichas de un jugador al empezar y al terminar la mano.
     * @param jugador nombre del jugador.
     * @param fichasAntes fichas al inicio de la mano.
     * @param fichasDespues fichas al final de la mano.
     */
    public record Participacion(String jugador, int fichasAntes, int fichasDespues) { }

    /**
     * Una jugada de la mano.
     * @param jugador nombre del jugador que actuó.
     * @param tipo acción: CHECK, CALL, FOLD o RAISE.
     * @param monto monto asociado (0 si no aplica).
     */
    public record Jugada(String jugador, String tipo, int monto) { }
}
