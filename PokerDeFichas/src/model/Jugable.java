package model;

import java.util.List;

/**
 * Contrato que debe cumplir cualquier implementación del juego de Póker.
 * Define los métodos necesarios para que la vista y el controlador interactúen.
 */
public interface Jugable {

    /**
     * Inicia una nueva ronda: baraja, reparte cartas privadas y reinicia el estado.
     */
    void iniciarRonda();

    /**
     * Procesa la acción de un jugador en su turno.
     * @param jugador el jugador que actúa
     * @param accion la acción elegida (CHECK, CALL, FOLD, RAISE)
     * @param cantidadApuesta si es RAISE, la cantidad a subir; en otros casos se ignora
     * @throws IllegalStateException si la acción no es válida en el estado actual
     */
    void procesarAccion(Jugador jugador, AccionPoker accion, int cantidadApuesta);

    /**
     * Avanza al siguiente jugador. Si es un bot, toma su decisión automáticamente
     * y repite hasta que el turno sea de un humano o termine la ronda.
     * Debe ser llamado por el controlador después de cada acción humana.
     */
    void siguienteTurno();

    /**
     * Devuelve el jugador que tiene el turno actual.
     * @return el jugador actual, o null si la ronda ha terminado
     */
    Jugador getJugadorActual();

    /**
     * @return lista de cartas comunitarias (flop, turn, river)
     */
    List<Carta> getCartasComunitarias();

    /**
     * @return monto actual del bote
     */
    int getBote();

    /**
     * @return apuesta máxima que se debe igualar en la ronda actual
     */
    int getApuestaActual();

    /**
     * @return true si la ronda de apuestas ha finalizado y se debe pasar a la siguiente fase
     */
    boolean isRondaTerminada();

    /**
     * Resuelve la ronda (showdown) y determina el ganador.
     * @return ResultadoRonda con el ganador y las fichas ganadas
     */
    ResultadoRonda resolverRonda();

    /**
     * @return lista de todos los jugadores en la partida
     */
    List<Jugador> getJugadores();

    /**
     * @return la fase actual del juego (PREFLOP, FLOP, TURN, RIVER, SHOWDOWN)
     */
    String getFaseActual();

    /**
     * @param jugador el jugador cuyas cartas privadas se quieren consultar
     * @return las 2 cartas privadas repartidas a ese jugador en la ronda actual,
     *         o una lista vacía si todavía no se le han repartido cartas
     */
    List<Carta> getCartasPrivadas(Jugador jugador);
}

